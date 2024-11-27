package com.example.lifestyle_data_app.service;

import com.example.lifestyle_data_app.dto.AuthorDTO;
import com.example.lifestyle_data_app.dto.SurveyDTO;
import com.example.lifestyle_data_app.dto.SurveyItemDTO;
import com.example.lifestyle_data_app.dto.SurveyMetaDataDTO;
import com.example.lifestyle_data_app.model.*;
import com.example.lifestyle_data_app.repository.*;
import com.example.lifestyle_data_app.utils.QuestionType;
import com.example.lifestyle_data_app.utils.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        metaData.setEditable(true); //tymczasowo
        result.setMetaData(metaData);

        return result;
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

            AuthorDTO author = setSurveyAuthor(log.getUser());

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
            metaData.setEditable(false); //jesli bedzie cos w logach to false, bo zostala wyslana juz uzytkownikom

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

}
