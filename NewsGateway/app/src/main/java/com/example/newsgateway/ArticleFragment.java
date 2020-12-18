package com.example.newsgateway;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.net.CookieHandler;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ArticleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArticleFragment extends Fragment {

    private String url = "";

    public ArticleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ArticleFragment newInstance(Article article, int index, int max) {
        ArticleFragment fragment = new ArticleFragment();
        Bundle args = new Bundle();
        args.putSerializable("ARTICLE", article);
        args.putSerializable("INDEX", index);
        args.putSerializable("TOTAL_COUNT", max);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment_layout = inflater.inflate(R.layout.fragment_article, container, false);

        Bundle args = getArguments();
        if (args != null) {
            final Article currentArticle = (Article) args.getSerializable("ARTICLE");
            if (currentArticle == null) {
                return null;
            }
            int index = args.getInt("INDEX");
            int total = args.getInt("TOTAL_COUNT");

            // get news url
            url = currentArticle.getArticle_url();

            // get news title
            TextView headline = fragment_layout.findViewById(R.id.article_headline);
            if (currentArticle.getTitle().isEmpty() || currentArticle.getTitle().equals("null")) {
                headline.setText("");
            } else {
                headline.setText(currentArticle.getTitle());
                headline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickImage();
                    }
                });
            }
            SimpleDateFormat formatIn;
            TextView data = fragment_layout.findViewById(R.id.article_date);
            if (currentArticle.getPublishedAt().isEmpty() || currentArticle.getPublishedAt().equals("null")) {
                data.setText("");
            } else {
                try {
                if(currentArticle.getPublishedAt().charAt(19) == '.') {
                    formatIn = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
 //               }
       //         }else if (currentArticle.getPublishedAt().charAt(20) == 'Z') {
  //                  formatIn = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                }else {
                    formatIn = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                //    formatIn = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+'ss:ss",Locale.getDefault());
                }
                    SimpleDateFormat formatOut = new SimpleDateFormat("MMMM dd, yyyy HH:mm", Locale.getDefault());
                    Date date = formatIn.parse(currentArticle.getPublishedAt());
                    String formatted = formatOut.format(date);
                    data.setText(formatted);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            TextView description = fragment_layout.findViewById(R.id.article_text);
            if ( currentArticle.getDescription().isEmpty() || currentArticle.getDescription().equals("null")) {
                description.setText("");
            }
            else {
                description.setText(currentArticle.getDescription());
                description.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickImage();
                    }
                });
            }


            TextView author = fragment_layout.findViewById(R.id.article_author);
            if (currentArticle.getAuthor().isEmpty() || currentArticle.getAuthor().equals("null")) {
                author.setText("");
            }else{
                author.setText(currentArticle.getAuthor());
            }

            /*
            TextView author = fragment_layout.findViewById(R.id.article_author);
            if (currentArticle.getAuthor().isEmpty() || currentArticle.getAuthor().equals("null")) {
                author.setText("");
            } else {
                author.setText(currentArticle.getAuthor());
            }
             */

            //Context context = getActivity().getApplicationContext();
            //Picasso picasso = new Picasso.Builder(context).build();
            //picasso.load(imageURL).error(R.drawable.no_image).placeholder(R.drawable.no_image).into(image);


            // page number counter
            TextView pageNum = fragment_layout.findViewById(R.id.page_num);
            pageNum.setText(String.format(Locale.US, "%d of %d", index, total));


            // get image
            ImageView image = fragment_layout.findViewById(R.id.article_image);
            if(currentArticle.getUrlToImage().isEmpty() || currentArticle.getUrlToImage().equals("null"))
            { }
            else {
                final String imageURL = currentArticle.getUrlToImage();

                Picasso.get().load(imageURL)
                        .into(image);
            }
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickImage();
                }
            });

            return fragment_layout;
        } else {
            return null;
        }
    }

    private void clickImage() {


        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);

    }

}