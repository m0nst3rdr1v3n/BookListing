package com.timothykocik.booklisting;

/**
 * Created by jenniferkocik on 10/19/16.
 */

public class ResultStrs {

    private String mTitle = "";
    private String mAuthor = "";
    private String mPublisher = "";


    public ResultStrs(String title, String author, String publisher) {
        mTitle = title;
        mAuthor = author;
        mPublisher = publisher;

    }

    // Getter Methods
    public String getTitle() {
        return mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getPublisher() {
        return mPublisher;
    }


}

