package gui.screens.articles_add;

import domain.modelo.Article;
import domain.modelo.ArticleType;
import domain.modelo.Newspaper;
import domain.services.ServicesArticles;
import domain.services.ServicesNewspapers;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class ArticlesAddViewModel {
    private final ServicesArticles servicesArticles;
    private final ServicesNewspapers servicesNewspapers;
    private final ObjectProperty<ArticlesAddState> state;
    private final ObservableList<Article> observableArticles;
    private final ObservableList<ArticleType> observableArticleTypes;
    private final ObservableList<Newspaper> observableNewspapers;

    @Inject
    public ArticlesAddViewModel(ServicesArticles servicesArticles, ServicesNewspapers servicesNewspapers) {
        this.servicesArticles = servicesArticles;
        this.servicesNewspapers = servicesNewspapers;
        state = new SimpleObjectProperty<>(new ArticlesAddState(null, false));
        observableArticles = FXCollections.observableArrayList();
        observableArticleTypes = FXCollections.observableArrayList();
        observableNewspapers = FXCollections.observableArrayList();
    }

    public ReadOnlyObjectProperty<ArticlesAddState> getState() {
        return state;
    }

    public ObservableList<Article> getObservableArticles() {
        return FXCollections.unmodifiableObservableList(observableArticles);
    }

    public ObservableList<ArticleType> getObservableArticleTypes() {
        return FXCollections.unmodifiableObservableList(observableArticleTypes);
    }

    public ObservableList<Newspaper> getObservableNewspapers() {
        return FXCollections.unmodifiableObservableList(observableNewspapers);
    }

    public void loadArticles() {
        List<Article> articles = servicesArticles.getArticles();
        if (articles.isEmpty()) {
            state.set(new ArticlesAddState("There are no articles", false));
        } else {
            observableArticles.clear();
            observableArticles.setAll(articles);
        }
    }

    public void loadArticleTypes() {
        List<ArticleType> articleTypes = servicesArticles.getArticleTypes();
        if (articleTypes.isEmpty()) {
            state.set(new ArticlesAddState("There are no article types", false));
        } else {
            observableArticleTypes.clear();
            observableArticleTypes.setAll(articleTypes);
        }
    }

    public void loadNewspapers() {
        List<Newspaper> newspapers = servicesNewspapers.getNewspapers();
        if (newspapers.isEmpty()) {
            state.set(new ArticlesAddState("There are no newspapers", false));
        } else {
            observableNewspapers.clear();
            observableNewspapers.setAll(newspapers);
        }
    }

    public void addArticle(String idText, String nameText, ArticleType articleType, Newspaper newspaper) {
        if (idText.isEmpty() || nameText.isEmpty() || articleType == null || newspaper == null) {
            state.set(new ArticlesAddState("Please fill all the fields", false));
        } else {
            try {
                int id = Integer.parseInt(idText);
                Article article = new Article(id, nameText, articleType.getId(), newspaper.getId());
                Either<String, Boolean> result = servicesArticles.saveArticle(article);
                if (result.isRight()) {
                    state.set(new ArticlesAddState(null, true));
                    loadArticles();
                } else {
                    state.set(new ArticlesAddState(result.getLeft(), false));
                }
            } catch (NumberFormatException e) {
                state.set(new ArticlesAddState("The id must be a number", false));
            }
        }
    }

    public void clearState() {
        state.set(new ArticlesAddState(null, false));
    }
}
