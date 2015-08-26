package com.pivotal.bootcamp;

/**
 * Created by administrator on 2015-08-26.
 */

import java.io.Serializable;
import java.util.List;

public class SomePOJO implements Serializable {
    public String title;
    public List<Dataset> dataset;

    public class Dataset implements Serializable{
        String curator_title;
        String curator_tagline;
    }
}
