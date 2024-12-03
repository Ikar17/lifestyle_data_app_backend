package com.example.lifestyle_data_app.service;

import com.example.lifestyle_data_app.dto.*;
import com.example.lifestyle_data_app.model.*;
import com.example.lifestyle_data_app.repository.*;
import com.example.lifestyle_data_app.utils.QuestionType;
import com.example.lifestyle_data_app.utils.Role;
import com.example.lifestyle_data_app.utils.SurveyStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SurveyService {
    @Autowired
    private SurveyRepository surveyRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerOptionRepository answerOptionRepository;
    @Autowired
    private SurveyLogRepository surveyLogRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SurveyResponseRepository surveyResponseRepository;
    @Autowired
    private AnswerRepository answerRepository;

    @Transactional
    public void createSurvey(SurveyDTO surveyDTO) throws Exception{
        User user = getUser();

        Survey survey = new Survey();
        survey.setTitle(surveyDTO.getTitle());
        survey.setDescription(surveyDTO.getDescription());
        survey.setAuthor(user);

        Survey surveySaved = surveyRepository.save(survey);

        for(SurveyItemDTO item : surveyDTO.getItems()){
            saveQuestion(item, surveySaved);
        }
    }

    public List<SurveyMetaDataDTO> getSurveys() throws Exception{
        User user = getUser();
        Role userRole = user.getRole();

        if(userRole.equals(Role.ADMIN)){
            return getSurveysForAdmin();
        }else if(userRole.equals(Role.ANALYST)){
            return getSurveysForAnalyst(user);
        }else{
            return getSurveysForUser(user);
        }
    }

    public SurveyDTO getSurveyById(Long id){
        Optional<Survey> surveyOptional = surveyRepository.findById(id);
        if(surveyOptional.isEmpty()) return null;
        Survey survey = surveyOptional.get();

        SurveyDTO result = new SurveyDTO();
        result.setTitle(survey.getTitle());
        result.setDescription(survey.getDescription());

        ArrayList<SurveyItemDTO> items = new ArrayList<>();

        List<Question> questions = questionRepository.findAllBySurvey(survey);
        for(Question question : questions){
            SurveyItemDTO item = new SurveyItemDTO();
            item.setText(question.getDescription());
            item.setType(question.getQuestionType().toString());
            item.setId(question.getId());

            ArrayList<String> options = new ArrayList<>();
            for(AnswerOption option : question.getAnswerOptions()){
                options.add(option.getAnswer());
            }
            item.setOptions(options);
            items.add(item);
        }

        result.setItems(items);

        SurveyMetaDataDTO metaData = new SurveyMetaDataDTO();
        metaData.setSurvey(survey);
        metaData.setAuthor(setSurveyAuthor(survey.getAuthor()));
        List<SurveyLog> logs = surveyLogRepository.findAllBySurvey_Id(id);
        if(logs.size() > 0) metaData.setEditable(false);
        else metaData.setEditable(true);

        result.setMetaData(metaData);

        return result;
    }

    public List<Answer> getSurveyResponse(Long surveyLogId) throws Exception{
        Optional<SurveyResponse> response = surveyResponseRepository.findBySurveyLog_Id(surveyLogId);
        if(response.isEmpty()) throw new Exception("Not found survey response");
        return answerRepository.findAllBySurveyResponse_Id(response.get().getId());
    }

    @Transactional
    public boolean updateSurvey(Long id, SurveyDTO surveyDTO){
        Optional<Survey> surveyOptional = surveyRepository.findById(id);
        if(surveyOptional.isEmpty()) return false;
        Survey survey = surveyOptional.get();
        survey.setTitle(surveyDTO.getTitle());
        survey.setDescription(survey.getDescription());

        //sprawdzanie czy ankieta zostala rozeslana
        List<SurveyLog> logs = surveyLogRepository.findAllBySurvey_Id(id);
        if(logs.size() > 0) return false;

        //usuwanie wszystkich pytan
        questionRepository.removeAllBySurvey_Id(id);

        //wstawienie nowych pytań
        for(SurveyItemDTO item : surveyDTO.getItems()){
            saveQuestion(item, survey);
        }
        surveyRepository.save(survey);
        return true;
    }

    @Transactional
    public void sendingSurvey(SurveySendDTO surveySendDTO) throws Exception {
        // Pobieram ankietę
        Optional<Survey> surveyOptional = surveyRepository.findById(surveySendDTO.getSurveyId());
        if (surveyOptional.isEmpty()) {
            throw new Exception("Survey not found");
        }
        Survey survey = surveyOptional.get();

        // Pobieram listę użytkowników po adresie
        List<User> users = userRepository.findUsersByAddress(
                surveySendDTO.getVoivodeship(),
                surveySendDTO.getDistrict(),
                surveySendDTO.getCommune()
        );

        // Tworzenie logów
        if (surveySendDTO.getIsOneTime()) {
            // Jednorazowe wysyłanie ankiety
            createSurveyLogsForDate(users, survey, LocalDate.now());
        } else {
            // Cykliczne wysyłanie ankiety w zakresie dat
            LocalDate currentDate = surveySendDTO.getStartDate();
            while (!currentDate.isAfter(surveySendDTO.getEndDate())) {
                createSurveyLogsForDate(users, survey, currentDate);
                currentDate = currentDate.plusDays(1);  // Przejście do kolejnego dnia
            }
        }
    }

    @Transactional
    public void saveResponse(SurveyResponseDTO responseDTO) throws Exception{
        //update log
        Optional<SurveyLog> logOptional = surveyLogRepository.findById(responseDTO.getSurveyLogId());
        if(logOptional.isEmpty()) throw new Exception("Not found that survey log");
        SurveyLog log = logOptional.get();
        log.setStatus(SurveyStatus.COMPLETE);
        surveyLogRepository.save(log);

        //save metadata about response
        SurveyResponse response = new SurveyResponse();
        response.setUser(getUser());
        response.setSurveyLog(log);

        response = surveyResponseRepository.save(response);

        //save answers
        for(SurveyItemDTO item : responseDTO.getAnswers()){
            Optional<Question> questionOptional = questionRepository.findById(item.getId());
            if(questionOptional.isEmpty()) throw new Exception("Not found question");

            Answer answer = new Answer();
            answer.setSurveyResponse(response);
            answer.setQuestion(questionOptional.get());
            if(item.getType().equals(QuestionType.TEXT.toString())){
                answer.setAnswer(item.getText());
            }else{
                List<AnswerOption> availableOptions = questionOptional.get().getAnswerOptions();
                List<AnswerOption> results = new ArrayList<>();
                for(AnswerOption option : availableOptions){
                    for(String receivingAnswer : item.getOptions()){
                        if(receivingAnswer.equals(option.getAnswer())){
                            results.add(option);
                            break;
                        }
                    }
                }
                answer.setAnswerOption(results);
            }
            answerRepository.save(answer);
        }
    }

    @Transactional
    public void deleteSurveyById(Long surveyId){
        answerRepository.removeAllBySurveyResponse_SurveyLog_Survey_Id(surveyId);
        questionRepository.removeAllBySurvey_Id(surveyId);
        surveyResponseRepository.removeAllBySurveyLog_Survey_Id(surveyId);
        surveyLogRepository.removeAllBySurvey_Id(surveyId);
        surveyRepository.deleteById(surveyId);
    }

    public SurveyResultsDTO getSurveyResultsById(Long surveyId) throws Exception{
        Optional<Survey> surveyOptional = surveyRepository.findById(surveyId);
        if(surveyOptional.isEmpty()) throw new Exception("Not found survey");
        Survey survey = surveyOptional.get();

        SurveyResultsDTO results = new SurveyResultsDTO();
        results.setSurveyId(surveyId);
        results.setTitle(survey.getTitle());
        results.setSentCount(surveyLogRepository.countAllBySurvey_IdAndStatus(surveyId, SurveyStatus.SENT));
        results.setCompleteCount(surveyLogRepository.countAllBySurvey_IdAndStatus(surveyId, SurveyStatus.COMPLETE));
        results.setQuestions(new ArrayList<>());

        List<Question> questions = questionRepository.findAllBySurvey(survey);
        for(Question question : questions){
            SurveyResultsDTO.QuestionDTO questionItem = new SurveyResultsDTO.QuestionDTO();
            questionItem.setQuestion(question.getDescription());
            questionItem.setQuestionType(question.getQuestionType().toString());
            questionItem.setResults(new ArrayList<>());

            if(question.getQuestionType().equals(QuestionType.TEXT)){
                List<Answer> answers = answerRepository.findAllByQuestion(question);
                for(Answer answer : answers){
                    SurveyResultsDTO.AnswerResultDTO answerItem = new SurveyResultsDTO.AnswerResultDTO();
                    answerItem.setAnswer(answer.getAnswer());
                    answerItem.setCount(1L);

                    questionItem.getResults().add(answerItem);
                }
            }else{
                for(AnswerOption answerOption : question.getAnswerOptions()){
                    SurveyResultsDTO.AnswerResultDTO answerItem = new SurveyResultsDTO.AnswerResultDTO();
                    answerItem.setAnswer(answerOption.getAnswer());
                    answerItem.setCount(answerRepository.countAllByQuestionAndAnswerOption(question, answerOption));

                    questionItem.getResults().add(answerItem);
                }
            }

            results.getQuestions().add(questionItem);
        }

        return results;
    }

    private void saveQuestion(SurveyItemDTO item, Survey survey){
        Question question = new Question();
        question.setSurvey(survey);
        question.setDescription(item.getText());

        if(item.getType().equals("SINGLE_CHOICE")){
            question.setQuestionType(QuestionType.SINGLE_CHOICE);
        }else if(item.getType().equals("MULTIPLE_CHOICE")){
            question.setQuestionType(QuestionType.MULTIPLE_CHOICE);
        }else{
            question.setQuestionType(QuestionType.TEXT);
        }

        Question questionSaved = questionRepository.save(question);

        for(String option : item.getOptions()){
            AnswerOption answerOption = new AnswerOption();
            answerOption.setAnswer(option);
            answerOption.setQuestion(questionSaved);
            answerOptionRepository.save(answerOption);
        }
    }
    private User getUser() throws Exception{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> userOptional = userRepository.findByUid(authentication.getName());
        if (userOptional.isEmpty()) throw new Exception("User not found");
        return userOptional.get();
    }
    private List<SurveyMetaDataDTO> getSurveysForUser(User user){
        List<SurveyMetaDataDTO> results = new ArrayList<>();
        List<SurveyLog> logs = surveyLogRepository.findAllByUser(user);
        for(SurveyLog log : logs){
            SurveyMetaDataDTO metaData = new SurveyMetaDataDTO();

            AuthorDTO author = setSurveyAuthor(log.getSurvey().getAuthor());

            metaData.setAuthor(author);
            metaData.setSurvey(log.getSurvey());
            metaData.setSurveyLog(log);
            metaData.setEditable(false);

            results.add(metaData);
        }
        return results;
    }

    private List<SurveyMetaDataDTO> getSurveysForAnalyst(User user){
        List<SurveyMetaDataDTO> results = new ArrayList<>();

        AuthorDTO author = setSurveyAuthor(user);

        List<Survey> surveys = surveyRepository.getAllByAuthor(user);
        for(Survey survey : surveys){
            SurveyMetaDataDTO metaData = new SurveyMetaDataDTO();
            metaData.setSurvey(survey);
            metaData.setAuthor(author);
            metaData.setEditable(false);

            results.add(metaData);
        }
        return results;
    }

    private List<SurveyMetaDataDTO> getSurveysForAdmin(){
        List<SurveyMetaDataDTO> results = new ArrayList<>();
        List<Survey> surveys = surveyRepository.findAll();
        for(Survey survey : surveys){
            SurveyMetaDataDTO metaData = new SurveyMetaDataDTO();

            AuthorDTO author = setSurveyAuthor(survey.getAuthor());

            metaData.setAuthor(author);
            metaData.setSurvey(survey);
            metaData.setEditable(false); //jesli bedzie cos w logach to false, bo zostala wyslana juz uzytkownikom

            results.add(metaData);
        }
        return results;
    }

    private AuthorDTO setSurveyAuthor(User user){
        AuthorDTO author = new AuthorDTO();
        author.setEmail(user.getEmail());
        author.setName(user.getName());
        author.setSurname(user.getSurname());
        return author;
    }

    private void createSurveyLogsForDate(List<User> users, Survey survey, LocalDate date) {
        for (User user : users) {
            SurveyLog log = new SurveyLog();
            log.setSurvey(survey);
            log.setUser(user);
            log.setStatus(SurveyStatus.SENT);
            log.setSendAt(date);

            surveyLogRepository.save(log);
        }
    }

}
