package nh.recipify.domain.api;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import nh.recipify.domain.model.Feedback;
import nh.recipify.domain.model.FeedbackRepository;
import nh.recipify.domain.model.Recipe;
import nh.recipify.domain.model.RecipeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/api")
@CrossOrigin(origins = "http://localhost:8090")
public class RecipeController {

    private static final Logger log = LoggerFactory.getLogger(RecipeController.class);

    private final RecipeRepository recipeRepository;
    private final FeedbackRepository feedbackRepository;

    public static long slowDown_GetRecipeList = 2400;
    public static long slowDown_GetRecipe = 2400;
    public static long slowDown_GetFeedbacks = 1600;

    public RecipeController(RecipeRepository recipeRepository, FeedbackRepository feedbackRepository) {
        this.recipeRepository = recipeRepository;
        this.feedbackRepository = feedbackRepository;
    }

    enum ReceipeSort {
        time,
        rating
    }

    @GetMapping("/recipes")
    PageResponse<RecipeDto> recipes(Optional<Integer> page,
                                    Optional<Integer> size,
                                    Optional<ReceipeSort> sort) {

        sleepFor(slowDown_GetRecipeList);

        var pageable = PageRequest.of(page.orElse(0),
            size.orElse(6),
            sort.map(s -> {
                if (s == ReceipeSort.time) {
                    return Sort.by("cookTime").and(Sort.by("preparationTime"));
                }
                return Sort.by("averageRating").descending().and(Sort.by("title"));
            }).orElse(Sort.by("createdAt").descending()));

        Page<Recipe> result = recipeRepository.findAllBy(pageable);
        var newPage = result.map(RecipeDto::forRecipe);
        return PageResponse.of(newPage);
    }

    record GetRecipeResponse(@NotNull DetailedRecipeDto recipe) {
    }

    @GetMapping("/recipes/{recipeId}")
    GetRecipeResponse getRecipe(
        @StringParameter
        @PathVariable long recipeId) {

        sleepFor(slowDown_GetRecipe);

        var recipe = recipeRepository.findById(recipeId)
            .orElseThrow(() -> new EntityNotFoundException("Receipe not found."));

        return new GetRecipeResponse(
            DetailedRecipeDto.of(recipe)
        );
    }

    record GetRecipeFeedbacksResponse(@NotNull List<Feedback> feedbacks) {
    }

    @GetMapping("/recipes/{recipeId}/feedbacks")
    GetRecipeFeedbacksResponse getFeedbacks(@StringParameter @PathVariable long recipeId) {
        sleepFor(slowDown_GetFeedbacks);

        var feedbacks = feedbackRepository.getFeedbacksByRecipeIdOrderByCreatedAtDesc(recipeId);

        return new GetRecipeFeedbacksResponse(feedbacks);
    }

    void sleepFor(long duration) {
        if (duration > 0) {
            log.info("Sleep for {} ms.", duration);
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


}