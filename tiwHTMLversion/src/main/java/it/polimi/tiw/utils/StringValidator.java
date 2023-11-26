package it.polimi.tiw.utils;

import it.polimi.tiw.exceptions.StringValidatorException;

import java.util.regex.Pattern;

public class StringValidator {

    /**
     * regular pattern verifier
     * @param string string on which the verification is carried out
     * @param regexPattern regular pattern to perform the verification
     * @return true if the string respect all the parameters specified in the regular pattern
     */
    private static boolean patternMatches(String string, String regexPattern) {
        return !Pattern.compile(regexPattern).matcher(string).matches();
    }

    /**
     * verify if the insert one is a real email with regular pattern
     * @param mail the inserted email
     * @throws StringValidatorException if the inserted mail doesn't appear as a real mail
     */
    public static void emailCheck(String mail) throws StringValidatorException {
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        if(StringValidator.patternMatches(mail, regexPattern)) throw new StringValidatorException("please insert a valid email");
    }

    /**
     * check if the username respect all the defined criteria:
     * composed of letters only
     * multiple words separated with _ or .
     * @param username the inserted username
     * @throws StringValidatorException if the username don't respect the criteria
     */
    public static void usernameCheck(String username) throws StringValidatorException {
        String regexPattern = "(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$";
        if(StringValidator.patternMatches(username, regexPattern)) throw new StringValidatorException("please insert a valid username");
    }

    public static void categoryNameSpecialCharactersFilter(String username) throws StringValidatorException {
        String regexPattern = "(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._ ]+(?<![_.])$";
        if(StringValidator.patternMatches(username, regexPattern)) throw new StringValidatorException("please insert a valid username");
    }

    /**
     * check if the password respect all the defined criteria:
     * at least 8 characters
     * at least one special character
     * at least one number
     * at least one upper case character
     * at least one lower case character
     * @param pwd the inserted password
     * @throws StringValidatorException if the password don't respect the criteria
     */
    public static void pwdCheck(String pwd) throws StringValidatorException {
        String regexPattern = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";
        if(StringValidator.patternMatches(pwd, regexPattern)) throw new StringValidatorException("password must contain at least 1 upper case letter, 1 lower case letter, one digit, one special character. minimum length 8 character");
    }

    /**
     * verify if the user has insert 2 identical password
     * @param pwd the first inserted password
     * @param pwd2 the second inserted password
     * @throws StringValidatorException if the two password aren't identical
     */
    public static void pwdCompare(String pwd, String pwd2) throws StringValidatorException {
        if(!pwd.equals(pwd2)) throw new StringValidatorException("the required password does not match");
    }

}
