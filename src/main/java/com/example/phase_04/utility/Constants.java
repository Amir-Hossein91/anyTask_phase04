package com.example.phase_04.utility;

public class Constants {
    public static final String INCORRECT_PASSWORD = "Incorrect current password";
    public static final String INVALID_USERNAME = "No person found with this username";
    public static final String INCORRECT_USERNAME_PASSWORD = "Incorrect username or password";
    public static final String ASSISTANCE_ALREADY_EXISTS = "Assistance already exists";
    public static final String NO_SUCH_ASSISTANCE_CATEGORY = "Assistance category does not exist";
    public static final String SUBASSISTANCE_ALREADY_EXISTS = "Sub-assistance already exists";
    public static final String TECHNICIAN_OR_SUBASSISTANCE_NOT_FOUND = "Technician/sub-assistance not found";
    public static final String ASSISTANCE_NOT_FOUND = "Assistance not found";
    public static final String DEACTIVATED_TECHNICIAN = "Technician profile is not active";
    public static final String DUPLICATE_TECHNICIAN_SUBASSISTANCE = "Technician is already assigned to the sub-assistance";
    public static final String TECHNICIAN_NOT_IN_LIST = "Technician is not assigned to this sub-assistance";
    public static final String NO_SUCH_SUBASSISTANCE = "Sub-assistance title does not exist";
    public static final String NO_UNAPPROVED_TECHNICIANS = "There is no unapproved technician";
    public static final String NO_DEACTIVATED_TECHNICIANS = "There is no deactivated technician";
    public static final String INVALID_SUGGESTED_PRICE = "Suggested price can not be less than base price";
    public static final String DATE_BEFORE_NOW = "Customer desired date/time can not be before current date/time";
    public static final String DATE_BEFORE_CUSTOMER_DESIRED = "Technician suggested date/time can not be before customer desired date/time";
    public static final String NO_RELATED_ORDERS = "There are no related orders related to this technician";
    public static final String NO_ORDERS_FOR_CUSTOMER = "Customer has no registered orders";
    public static final String ORDER_IS_NOT_RELATED = "This order is not related to this technician";
    public static final String NO_SUCH_ORDER = "No order exists with this id";
    public static final String ORDER_NOT_BELONG_TO_CUSTOMER = "This order does not belong to this customer";
    public static final String NO_TECHNICIAN_SUGGESTION_FOUND = "This order does not have any technician suggestions yet";
    public static final String SUGGESTION_NOT_AVAILABLE_IN_THIS_STATUS = "Suggestions are not available in this order status";
    public static final String TECHNICIAN_SUGGESTION_NOT_EXIST = "This technician suggestion id does not exist";
    public static final String TECHNICIAN_SUGGESTION_NOT_IN_LIST = "This technician suggestion id is not in this order's list";
    public static final String PAYING_NOT_POSSIBLE_IN_THIS_STATE = "Paying is not possible in this state of order";
    public static final String SCORING_NOT_POSSIBLE_IN_THIS_STATE = "Scoring the technician is only possible when order is 'Finished' ";
    public static final String NOT_ENOUGH_CREDIT = "Not enough credit";
    public static final String NO_TECHNICIAN_SELECTED = "No technician is selected yet";
    public static final String SUGGESTION_IS_NOT_THE_CHOSEN_ONE = "This suggestion is not the one tha customer has chosen";
    public static final String ORDER_NOT_STARTED = "Order status can not be set as 'finished' before it gets started";
}
