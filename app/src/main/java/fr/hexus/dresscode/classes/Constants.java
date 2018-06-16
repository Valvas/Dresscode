package fr.hexus.dresscode.classes;

public final class Constants
{
    public static final String WARDROBE_TABLE_NAME = "wardrobe";
    public static final String WARDROBE_TABLE_COLUMNS_TYPE = "type";
    public static final String WARDROBE_TABLE_COLUMNS_PATH = "path";
    public static final String WARDROBE_TABLE_COLUMNS_COLOR = "color";
    public static final String WARDROBE_TABLE_COLUMNS_OUTFIT = "outfit";

    public static final String OUTFIT_TABLE_NAME = "outfit";
    public static final String OUTFIT_TABLE_COLUMNS_NAME = "name";

    public static final String SHARED_PREFERENCES_FILE_NAME = "customPreferences";

    public static final String API_BASE_URL = "http://dresscode.ddns.net";

    public static final String EMAIL_REGEX_FORMAT = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
    public static final String PASSWORD_REGEX_FORMAT = "(?=.{6,24}$)[a-zA-Z0-9]*";
    public static final String LASTNAME_REGEX_FORMAT = "[a-zA-Zéèàùâêîôûäëïöüñ]{2,}(-)?[a-zA-Zéèàùâêîôûäëïöüñ]+";
    public static final String FIRSTNAME_REGEX_FORMAT = "[a-zA-Zéèàùâêîôûäëïöüñ]{2,}(-)?[a-zA-Zéèàùâêîôûäëïöüñ]+";
}
