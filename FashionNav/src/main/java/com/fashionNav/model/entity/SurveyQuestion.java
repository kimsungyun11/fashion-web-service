package com.fashionNav.model.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SurveyQuestion {
    private int questionId;
    private int surveyId;
    private String questionText;


}