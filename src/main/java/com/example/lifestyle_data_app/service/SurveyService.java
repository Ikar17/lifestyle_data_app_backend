package com.example.lifestyle_data_app.service;

import com.example.lifestyle_data_app.dto.*;
import com.example.lifestyle_data_app.model.*;
import com.example.lifestyle_data_app.repository.*;
import com.example.lifestyle_data_app.utils.QuestionType;
import com.example.lifestyle_data_app.utils.Role;
import com.example.lifestyle_data_app.utils.SurveyStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    public Page<SurveyMetaDataDTO> getSurveys(int page, int size, String sort) throws Exception{
        User user = getUser();
        Role userRole = user.getRole();

        boolean ascending = sort.equals("asc");

        if(userRole.equals(Role.ADMIN)){
            return getSurveysForAdmin(page, size, ascending);
        }else if(userRole.equals(Role.ANALYST)){
            return getSurveysForAnalyst(user, page, size, ascending);
        }else{
            return getSurveysForUser(user, page, size, ascending);
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

    public SurveyResultsDTO getSurveyResultsById(Long surveyId,
                                                 String startDateString,
                                                 String endDateString,
                                                 String voivodeship,
                                                 String district,
                                                 String comunne) throws Exception{
        Optional<Survey> surveyOptional = surveyRepository.findById(surveyId);
        if(surveyOptional.isEmpty()) throw new Exception("Not found survey");
        Survey survey = surveyOptional.get();

        //date filters
        LocalDateTime startDate = LocalDateTime.of(2024,10,1,0,0);
        LocalDateTime endDate = LocalDateTime.now();
        try{
            if(!startDateString.equals("")) startDate = LocalDateTime.parse(startDateString);
            if(!endDateString.equals("")) endDate = LocalDateTime.parse(endDateString);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }


        SurveyResultsDTO results = new SurveyResultsDTO();
        results.setSurveyId(surveyId);
        results.setTitle(survey.getTitle());

        long surveyCompleteCount;
        long surveySentCount;
        if(!comunne.equals("")){
            surveyCompleteCount = surveyLogRepository.countAllBySurvey_IdAndStatusAndSendAtBetweenAndUser_Address_Comunne_Name(surveyId, SurveyStatus.COMPLETE, startDate.toLocalDate(), endDate.toLocalDate(), comunne);
            surveySentCount = surveyLogRepository.countAllBySurvey_IdAndStatusAndSendAtBetweenAndUser_Address_Comunne_Name(surveyId, SurveyStatus.SENT, startDate.toLocalDate(), endDate.toLocalDate(), comunne) + surveyCompleteCount;
        }else if(!district.equals("")){
            surveyCompleteCount = surveyLogRepository.countAllBySurvey_IdAndStatusAndSendAtBetweenAndUser_Address_District_Name(surveyId, SurveyStatus.COMPLETE, startDate.toLocalDate(), endDate.toLocalDate(), district);
            surveySentCount = surveyLogRepository.countAllBySurvey_IdAndStatusAndSendAtBetweenAndUser_Address_District_Name(surveyId, SurveyStatus.SENT, startDate.toLocalDate(), endDate.toLocalDate(), district) + surveyCompleteCount;
        }else if(!voivodeship.equals("")){
            surveyCompleteCount = surveyLogRepository.countAllBySurvey_IdAndStatusAndSendAtBetweenAndUser_Address_Voivodeship_Name(surveyId, SurveyStatus.COMPLETE, startDate.toLocalDate(), endDate.toLocalDate(), voivodeship);
            surveySentCount = surveyLogRepository.countAllBySurvey_IdAndStatusAndSendAtBetweenAndUser_Address_Voivodeship_Name(surveyId, SurveyStatus.SENT, startDate.toLocalDate(), endDate.toLocalDate(), voivodeship) + surveyCompleteCount;
        } else{
            surveyCompleteCount = surveyLogRepository.countAllBySurvey_IdAndStatusAndSendAtBetween(surveyId, SurveyStatus.COMPLETE, startDate.toLocalDate(), endDate.toLocalDate());
            surveySentCount = surveyLogRepository.countAllBySurvey_IdAndStatusAndSendAtBetween(surveyId, SurveyStatus.SENT, startDate.toLocalDate(), endDate.toLocalDate()) + surveyCompleteCount;
        }
        results.setCompleteCount(surveyCompleteCount);
        results.setSentCount(surveySentCount);

        results.setQuestions(new ArrayList<>());

        List<Question> questions = questionRepository.findAllBySurvey(survey);
        for(Question question : questions){
            SurveyResultsDTO.QuestionDTO questionItem = new SurveyResultsDTO.QuestionDTO();
            questionItem.setQuestion(question.getDescription());
            questionItem.setQuestionType(question.getQuestionType().toString());
            questionItem.setResults(new ArrayList<>());

            if(question.getQuestionType().equals(QuestionType.TEXT)){
                List<Answer> answers;
                if(!comunne.equals("")){
                    answers = answerRepository.findAllByQuestionAndSurveyResponse_CreatedAtBetweenAndSurveyResponse_User_Address_Comunne_Name(
                            question,
                            startDate,
                            endDate,
                            comunne
                    );
                }else if(!district.equals("")){
                    answers = answerRepository.findAllByQuestionAndSurveyResponse_CreatedAtBetweenAndSurveyResponse_User_Address_District_Name(
                            question,
                            startDate,
                            endDate,
                            district
                    );
                }else if(!voivodeship.equals("")){
                    answers = answerRepository.findAllByQuestionAndSurveyResponse_CreatedAtBetweenAndSurveyResponse_User_Address_Voivodeship_Name(
                            question,
                            startDate,
                            endDate,
                            voivodeship
                    );
                } else{
                    answers = answerRepository.findAllByQuestionAndSurveyResponse_CreatedAtBetween(
                            question,
                            startDate,
                            endDate
                    );
                }

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
                    if(!comunne.equals("")){
                        answerItem.setCount(answerRepository.countAllByQuestionAndAnswerOptionAndSurveyResponse_CreatedAtBetweenAndSurveyResponse_User_Address_Comunne_Name(
                                question,
                                answerOption,
                                startDate,
                                endDate,
                                comunne)
                        );
                    }else if(!district.equals("")){
                        answerItem.setCount(answerRepository.countAllByQuestionAndAnswerOptionAndSurveyResponse_CreatedAtBetweenAndSurveyResponse_User_Address_District_Name(
                                question,
                                answerOption,
                                startDate,
                                endDate,
                                district)
                        );
                    }else if(!voivodeship.equals("")){
                        answerItem.setCount(answerRepository.countAllByQuestionAndAnswerOptionAndSurveyResponse_CreatedAtBetweenAndSurveyResponse_User_Address_Voivodeship_Name(
                                question,
                                answerOption,
                                startDate,
                                endDate,
                                voivodeship)
                        );
                    } else{
                        answerItem.setCount(answerRepository.countAllByQuestionAndAnswerOptionAndSurveyResponse_CreatedAtBetween(
                                question,
                                answerOption,
                                startDate,
                                endDate)
                        );
                    }
                    questionItem.getResults().add(answerItem);
                }
            }

            results.getQuestions().add(questionItem);
        }

        return results;
    }

    @Transactional
    public void removeAllByUser(User user){
        answerRepository.removeAllBySurveyResponse_User(user);
        surveyResponseRepository.removeAllByUser(user);
        surveyLogRepository.removeAllByUser(user);

        List<Survey> surveys = surveyRepository.getAllByAuthor(user);
        for(Survey survey : surveys){
            deleteSurveyById(survey.getId());
        }
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
    private Page<SurveyMetaDataDTO> getSurveysForUser(User user, int page, int size, boolean ascending){
        List<SurveyMetaDataDTO> results = new ArrayList<>();

        Sort sort = ascending ? Sort.by("sendAt").ascending() : Sort.by("sendAt").descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        LocalDate date = LocalDate.now().plusDays(1);

        Page<SurveyLog> logs = surveyLogRepository.findAllByUserAndSendAtBefore(user, date, pageable);
        for(SurveyLog log : logs.getContent()){
            SurveyMetaDataDTO metaData = new SurveyMetaDataDTO();

            AuthorDTO author = setSurveyAuthor(log.getSurvey().getAuthor());

            metaData.setAuthor(author);
            metaData.setSurvey(log.getSurvey());
            metaData.setSurveyLog(log);
            metaData.setEditable(false);

            results.add(metaData);
        }
        return new PageImpl<>(results,pageable,logs.getTotalElements());
    }

    private Page<SurveyMetaDataDTO> getSurveysForAnalyst(User user, int page, int size, boolean ascending){
        List<SurveyMetaDataDTO> results = new ArrayList<>();

        AuthorDTO author = setSurveyAuthor(user);

        Sort sort = ascending ? Sort.by("createdAt").ascending() : Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Survey> surveys = surveyRepository.getAllByAuthor(user, pageable);
        for(Survey survey : surveys.getContent()){
            SurveyMetaDataDTO metaData = new SurveyMetaDataDTO();
            metaData.setSurvey(survey);
            metaData.setAuthor(author);
            metaData.setEditable(false);

            results.add(metaData);
        }
        return new PageImpl<>(results,pageable,surveys.getTotalElements());
    }

    private Page<SurveyMetaDataDTO> getSurveysForAdmin(int page, int size, boolean ascending){
        List<SurveyMetaDataDTO> results = new ArrayList<>();

        Sort sort = ascending ? Sort.by("createdAt").ascending() : Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Survey> surveys = surveyRepository.findAll(pageable);
        for(Survey survey : surveys.getContent()){
            SurveyMetaDataDTO metaData = new SurveyMetaDataDTO();

            AuthorDTO author = setSurveyAuthor(survey.getAuthor());

            metaData.setAuthor(author);
            metaData.setSurvey(survey);
            metaData.setEditable(false); //jesli bedzie cos w logach to false, bo zostala wyslana juz uzytkownikom

            results.add(metaData);
        }
        return new PageImpl<>(results,pageable,surveys.getTotalElements());
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


    public void test(Long surveyId, PrintWriter writer) {
        List<SurveyResponse> responses = surveyResponseRepository.getAllBySurveyLog_Survey_Id(surveyId);

        writer.println(createCSVFileHeader(surveyId));

        for(SurveyResponse response : responses){
            User user = response.getUser();

            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(response.getCreatedAt().toString());
            stringBuilder.append(";");
            stringBuilder.append(user.getEmail());
            stringBuilder.append(";");
            stringBuilder.append(user.getAddress().getVoivodeship().getName());
            stringBuilder.append(";");
            stringBuilder.append(user.getAddress().getDistrict().getName());
            stringBuilder.append(";");
            stringBuilder.append(user.getAddress().getComunne().getName());
            stringBuilder.append(";");

            List<Answer> answers = answerRepository.findAllBySurveyResponse_Id(response.getId());

            for(Answer answer : answers){
                if(answer.getQuestion().getQuestionType().equals(QuestionType.TEXT)){
                    stringBuilder.append(answer.getAnswer());
                }else{
                    List<AnswerOption> options = answer.getAnswerOption();
                    if(options == null || options.size() == 0) stringBuilder.append(" ");
                    else{
                        for(AnswerOption option : answer.getAnswerOption()){
                            stringBuilder.append(option.getAnswer());
                            stringBuilder.append(", ");
                        }
                        stringBuilder.setLength(stringBuilder.length() - 2);
                    }
                }
                stringBuilder.append(";");
            }
            stringBuilder.setLength(stringBuilder.length() - 1);
            writer.println(stringBuilder);
        }
    }

    private String createCSVFileHeader(Long surveyId){
        List<Question> questions = questionRepository.findAllBySurvey_Id(surveyId);
        StringBuilder results = new StringBuilder();

        results.append("Data odpowiedzi;");
        results.append("Email;");
        results.append("Województwo;");
        results.append("Powiat;");
        results.append("Gmina;");

        for(Question question : questions){
            results.append(question.getDescription());
            results.append(";");
        }
        results.setLength(results.length() - 1);
        return results.toString();
    }

    public Page<SurveySendingStatsDTO> getSurveySendingStats(Long surveyId, int page, int size, String sortString){
        Sort sort = sortString.equals("desc") ? Sort.by("sendAt").descending() : Sort.by("sendAt").ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return surveyLogRepository.countSurveysBySurveyIdAndDates(surveyId, pageable);
    }

    @Transactional
    public void deleleSurveySendingByDate(Long surveyId, String dateString){
        if(dateString == null) return;

        LocalDate date = LocalDate.parse(dateString);
        LocalDateTime dateStart = date.atStartOfDay();
        LocalDateTime dateEnd = date.atTime(LocalTime.MAX);

        answerRepository.removeAllBySurveyResponse_CreatedAtBetweenAndSurveyResponse_SurveyLog_Survey_Id(dateStart, dateEnd, surveyId);
        surveyResponseRepository.removeAllByCreatedAtBetweenAndSurveyLog_Survey_Id(dateStart, dateEnd, surveyId);
        surveyLogRepository.removeAllBySurvey_IdAndSendAt(surveyId, date);
    }
}
