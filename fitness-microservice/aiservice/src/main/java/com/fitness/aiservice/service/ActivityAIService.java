package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIService
{
    private final GeminiService geminiService;

    public Recommendation generateRecommendation(Activity activity)
    {
        String prompt = createPromptForActivity(activity);
        String aiResponse = geminiService.getAnswer(prompt);
        log.info("RESPONSE FROM AI: {}", aiResponse);
        return processAiResponse(activity, aiResponse);
    }

    public Recommendation processAiResponse(Activity activity, String aiResponse)
    {
        //deserializing and extracting only the generated text from the response
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(aiResponse); //getting the response which is in json format into a tree form which java can understand

            //accessing the text node which we require
            JsonNode textNode = rootNode.path("steps")
                    .get(1)
                    .path("content")
                    .get(0)
                    .path("text");

            String jsonContent = textNode.asText()
                    .replaceAll("```json\\n", "")
                    .replaceAll("\\n```", "")
                    .trim();

           // log.info("PARSED RESPONSE FROM AI: {} ", jsonContent);

            JsonNode analysisJson = mapper.readTree(jsonContent);
            JsonNode analysisNode = analysisJson.path("analysis");//getting the analysis from the parsed response data
            StringBuilder fullAnalysis = new StringBuilder();//this contains the data of analysis section which we will save in the recommendation field of Recommendation model


            //Keys present in the analysis Node or section
            addAnalysisSection(fullAnalysis, analysisNode, "overall", "Overall:");
            addAnalysisSection(fullAnalysis, analysisNode, "pace", "Pace:");
            addAnalysisSection(fullAnalysis, analysisNode, "heartRate", "Heart Rate:");
            addAnalysisSection(fullAnalysis, analysisNode, "caloriesBurned", "Calories:");

            //now for improvements field in Recommendation model, we are extracting the improvements section
            List<String> improvements = extractImprovements(analysisJson.path("improvements"));

            //now, extracting the suggestions for saving into the suggestion field in Recommendation model
            List<String> suggestions = extractSuggestions(analysisJson.path("suggestions"));

            //extracting the safety guidelines for saving into the safety field in Recommendation model
            List<String> safety = extractSafetyGuideLines(analysisJson.path("safety"));

            //So, we have processed the data and now we will craft the Recommendation Object
            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .activityType(activity.getType())
                    .recommendation(fullAnalysis.toString().trim())
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .createdAt(LocalDateTime.now())
                    .build();


        }
        catch (Exception e)
        {
            e.printStackTrace();
            return createDeafultRecommendation(activity); //so, just in case processing fails and we are not able to genreate the recommendations then this default is returned

        }
    }

    private Recommendation createDeafultRecommendation(Activity activity)
    {
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(activity.getType())
                .recommendation("Unable to generate detailed analysis")
                .improvements(Collections.singletonList("Continue with your current routine"))
                .suggestions(Collections.singletonList("Consider consulting a fitness professional"))
                .safety(Arrays.asList(
                        "Always warm up before exercise",
                        "Stay Hydrated",
                        "Listen to your body"
                ))
                .createdAt(LocalDateTime.now())
                .build();

    }

    private List<String> extractSafetyGuideLines(JsonNode safetyNode)
    {
        List<String> safety = new ArrayList<>();
        if(safetyNode.isArray())
        {
            safetyNode.forEach(item -> safety.add(item.asText()) );
        }
        return safety.isEmpty() ?
                Collections.singletonList("Follow general Safety Guidelines") :
                safety;
    }

    private List<String> extractSuggestions(JsonNode suggestionsNode)
    {
        List<String> suggestions = new ArrayList<>();

        if(suggestionsNode.isArray())
        {
            suggestionsNode.forEach(suggestion -> {
                String workout = suggestion.path("workout").asText();  //key in improvement
                String description = suggestion.path("description").asText();
                suggestions.add(String.format("%s: %s", workout, description));//adding the improvement in th list in this format
            });
        }
        return suggestions.isEmpty() ?
                Collections.singletonList("No specific Suggestions provided") :
                suggestions;
    }

    private List<String> extractImprovements(JsonNode improvementsNode)
    {
        List<String> improvements = new ArrayList<>();
        if(improvementsNode.isArray())
        {
            improvementsNode.forEach(improvement -> {
                String area = improvement.path("area").asText();  //key in improvement
                String detail = improvement.path("recommendation").asText();
                improvements.add(String.format("%s: %s", area, detail));//adding the improvement in th list in this format
            });
        }
        return improvements.isEmpty() ?
                Collections.singletonList("No specific improvements provided") :
                improvements;
    }

    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix)
    {
        if(!analysisNode.path(key).isMissingNode())
        {
            fullAnalysis.append(prefix)
                    .append(analysisNode.path(key).asText())
                    .append("\n\n");
        }
    }

    private String createPromptForActivity(Activity activity)
    {
        //in this we will craft the prompt and we know that crafting ai model is a skill, So we will give prompt to the Ai and we will tell to give the data in a particular format and that format will be prreented to the activity model.


        //we are telling the ai model to give the response in the json format because in text format it will be tough to read it.
        return String.format("""
                Analyze this fitness activity and provide detailed recommendations in the following EXACT json format, Return only valid JSON. Do not wrap the response in markdown or code fences. Do not include any explanation before or after the JSON:
                {
                   "analysis": {
                       "overall": "Overall analysis here",
                       "pace": "Pace analysis herre",
                       "heartRate": "Heart rate analysis here",
                       "caloriesBurned": "Calories analysis here"
                     },
                     "improvements": [
                        {
                           "area": "Area name",
                           "recommendation": "Detailed recommendation"
                          }
                        ],
                        "suggestions": [
                            {
                              "workout": "Workout name",
                              "description": "Detailed workout description"
                             
                             }
                           ],
                           "safety": [
                             "Safety point 1",
                             "Safety point 2"
                            ]
                           }
                           
                           Analyze this activity:
                           Activity Type: %s
                           Duration: %d minutes
                           Calories: %d
                           Additional Mettrics: %s
                           
                           Provide detailed analysis focusing on performance, improvements, next workout suggestions, and safety guidelines.
                            Ensure the response follows the EXACT JSON format shown above.
                   """,
                                      activity.getType(),
                                       activity.getDuration(),
                                        activity.getCaloriesBurned(),
                                         activity.getAdditionalMetrics()
                                 );
    }
}
