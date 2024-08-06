package com.consubanco.freemarker;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

    public static final String FUNCTIONS_KEY = "FunctionsUtil";
    public static final String DATA_KEY = "object-with-data";
    public static final String TEMPLATE_KEY = "template";
    public static final String RESULT_KEY = "result";
    public static final String CLASS_KEY = "class-object-to-convert";
    public static final String EXCEPTION_KEY = "exception-message";
    public static final String CAUSE_KEY = "exception-cause";
    public static final String MESSAGE_ERROR = "Error processing template with Freemarker";
    public static final String MESSAGE_JSON_ERROR = "Error processing string to json with jackson";
    public static final String MESSAGE_SUCCESS = "This is the result of the freemarker template after processing";

}
