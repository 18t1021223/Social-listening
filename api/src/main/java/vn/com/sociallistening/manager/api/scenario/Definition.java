package vn.com.sociallistening.manager.api.scenario;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Definition implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String ACTION_LOAD_PAGE = "load_page";
    public static final String ACTION_GOTO_PAGE = "goto_page";
    public static final String ACTION_BACK_PAGE = "back_page";
    public static final String ACTION_FORWARD_PAGE = "forward_page";
    public static final String ACTION_SET = "set";
    public static final String ACTION_GET = "get";
    public static final String ACTION_GET_LIST = "get_list";
    public static final String ACTION_CLICK = "click";
    public static final String ACTION_BEGIN_CLICK_LIST = "begin_click_list";
    public static final String ACTION_END_CLICK_LIST = "end_click_list";
    public static final String ACTION_SCROLL_DOWN = "scroll_down";
    public static final String ACTION_SCREEN_MAX = "screen_max";
    public static final String ACTION_DECLARE_SINGLE_OBJECT = "declare_single_object";

    public static final String ACTION_DECLARE_LIST_OBJECT = "declare_list_object";
    public static final String ACTION_ADD_TO_MAP_OBJECT = "add_to_map_object";

    public static final String ACTION_BEGIN_UNLIMITED_LOOP = "begin_unlimited_loop";
    public static final String ACTION_BEGIN_LIMITED_LOOP = "begin_limited_loop";
    public static final String ACTION_END_LOOP = "end_loop";
    public static final String ACTION_SLEEP = "sleep";
//    public static final String ACTION_SELECT_OPTION = "select_option";
    public static final String ACTION_CONVERT_OBJECT = "convert_object";

    private String action;

    private String xpath;

    private String attribute;

    private String value;

    private String fieldName;

    private String next_page_element_xpath;

    private String next_page_action;

    private String child_xpath;

    private String list_child_xpath;

    private String type;

    private int index;

    private boolean ignore_exception ;
}
