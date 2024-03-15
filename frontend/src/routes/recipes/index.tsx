import { createFileRoute } from "@tanstack/react-router";
import RecipeListPage from "../../components/material/RecipeListPage.tsx";
import z from "zod";

const RecipePageListParams = z.object({
  page: z.number().min(0).optional(),
  orderBy: z.enum(["time", "rating"]).optional(),
  bookmarkedRecipeIds: z.string().array().optional(),
  showOnlyBookmarked: z.boolean().optional(),
});
type TRecipePageListParams = z.infer<typeof RecipePageListParams>;

// todo:
//  - add search params (copy from 70_RecipePageListParams.txt)
//  - add validateSearch
export const Route = createFileRoute("/recipes/")({
  component: RecipeListPage,
  validateSearch: (s) => RecipePageListParams.parse(s),
});