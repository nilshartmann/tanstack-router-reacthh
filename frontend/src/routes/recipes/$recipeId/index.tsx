import { createFileRoute } from "@tanstack/react-router";
import { fetchRecipe } from "../../../components/use-queries.ts";
import { RecipePageContent } from "../../../components/material/RecipePageContent.tsx";

export const Route = createFileRoute("/recipes/$recipeId/")({
  component: RecipePage,
  loader: async ({ params }) => {
    return fetchRecipe(params.recipeId);
    // params.recipeId
  },
});

function RecipePage() {
  /*  TODO:

    - Load data in 'loader' with 'recipeId' from 'params.recipeId'

    - use Route.useLoaderData() 

    - render: <RecipePageContent recipe={data.recipe} />
    
    */
  const { recipeId } = Route.useParams();

  const recipe = Route.useLoaderData();

  return (
    <div className={"space-y-8 p-8 text-4xl"}>
      <RecipePageContent recipe={recipe.recipe} />
    </div>
  );
}
