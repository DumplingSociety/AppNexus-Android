package com.example.newsgateway;

import java.io.Serializable;
public class Article implements Serializable {
    String author;
    String title;
    String description;
    String article_url;
    String urlToImage;
    String publishedAt;

    public Article(String author, String title, String description, String article_url, String urlToImage, String publishedAt)
    {
        this.author = author;
        this.title = title;
        this.description = description;
        this.article_url = article_url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
    }

    public String getAuthor() {return author;}

    public String getTitle() {return title;}

    public String getArticle_url() {return article_url;}

    public String getUrlToImage() {return urlToImage;}

    public String getPublishedAt() {return publishedAt;}

    public String getDescription() {return description;}
}
