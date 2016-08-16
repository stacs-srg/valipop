package uk.ac.standrews.cs.digitising_scotland.linkage.tools;

import java.util.Arrays;

/**
 *
 * Represents the fields in field order of DS records.
 *
 * Created by al on 24/04/15.
 */
public class DSFields {

    // Common fields

    public static final String ORIGINAL_ID = "ORIGINAL_ID";
    private static final String SURNAME = "surname";
    private static final String FORENAME = "forename";
    private static final String SEX = "sex";
    private static final String YEAR_OF_REGISTRATION = "YEAR_OF_REGISTRATION";
    private static final String REGISTRATION_DISTRICT_NUMBER = "REGISTRATION_DISTRICT_NUMBER";
    private static final String REGISTRATION_DISTRICT_SUFFIX = "REGISTRATION_DISTRICT_SUFFIX";
    private static final String ENTRY = "ENTRY";

    private static final String FATHERS_FORENAME = "fathers_forename";
    private static final String FATHERS_SURNAME = "fathers_surname";
    private static final String FATHERS_OCCUPATION = "fathers_occupation";
    private static final String MOTHERS_FORENAME = "mothers_forename";
    private static final String MOTHERS_SURNAME = "mothers_surname";

    private static final String MOTHERS_MAIDEN_SURNAME = "mothers_maiden_surname";
    private static final String CHANGED_SURNAME = "changed_surname";
    private static final String CHANGED_FORENAME = "changed_forename";
    private static final String CHANGED_MOTHERS_MAIDEN_SURNAME = "changed_mothers_maiden_surname";

    private static final String CORRECTED_ENTRY = "corrected_entry";
    private static final String IMAGE_QUALITY = "image_quality";

    // Birth fields

    private static final String BIRTH_YEAR = "birth_year";
    private static final String BIRTH_DAY = "birth_day";
    private static final String BIRTH_MONTH = "birth_month";
    private static final String BIRTH_ADDRESS = "birth_address";

    private static final String PARENTS_DAY_OF_MARRIAGE = "parents_day_of_marriage";
    private static final String PARENTS_MONTH_OF_MARRIAGE = "parents_month_of_marriage";
    private static final String PARENTS_YEAR_OF_MARRIAGE = "parents_year_of_marriage";
    private static final String PARENTS_PLACE_OF_MARRIAGE = "parents_place_of_marriage";
    private static final String ILLEGITIMATE_INDICATOR = "illegitimate_indicator";
    private static final String INFORMANT = "informant";
    private static final String INFORMANT_DID_NOT_SIGN =  "informant_did_not_sign";

    private static final String ADOPTION = "adoption";

    public static final Iterable<String> BIRTH_FIELD_NAMES = Arrays.asList(ORIGINAL_ID, SURNAME, FORENAME, SEX, YEAR_OF_REGISTRATION, REGISTRATION_DISTRICT_NUMBER,
            REGISTRATION_DISTRICT_SUFFIX, ENTRY, BIRTH_YEAR, MOTHERS_MAIDEN_SURNAME, CHANGED_SURNAME, CHANGED_FORENAME, BIRTH_DAY, BIRTH_MONTH,
            BIRTH_ADDRESS, FATHERS_FORENAME, FATHERS_SURNAME, FATHERS_OCCUPATION, MOTHERS_FORENAME, MOTHERS_SURNAME, CHANGED_MOTHERS_MAIDEN_SURNAME,
            PARENTS_DAY_OF_MARRIAGE, PARENTS_MONTH_OF_MARRIAGE, PARENTS_YEAR_OF_MARRIAGE, PARENTS_PLACE_OF_MARRIAGE, ILLEGITIMATE_INDICATOR, INFORMANT,
            INFORMANT_DID_NOT_SIGN, CORRECTED_ENTRY, ADOPTION, IMAGE_QUALITY);

    // Death fields

    private static final String DEATH_YEAR = "death_year";
    private static final String AGE_AT_DEATH = "age_at_death";
    private static final String CHANGED_DEATH_AGE = "changed_death_age";
    private static final String DATE_OF_BIRTH = "date_of_birth";
    private static final String OCCUPATION = "occupation";
    private static final String MARITAL_STATUS = "marital_status";
    private static final String SPOUSES_NAMES = "spouses_names";
    private static final String SPOUSES_OCCUPATIONS = "spouses_occupations";
    private static final String DEATH_MONTH = "death_month";
    private static final String DEATH_DAY = "death_day";
    private static final String PLACE_OF_DEATH = "place_of_death";
    private static final String FATHER_DECEASED = "father_deceased";
    private static final String MOTHER_DECEASED = "mother_deceased";
    private static final String COD_A = "cod_a";
    private static final String COD_B = "cod_b";
    private static final String COD_C = "cod_c";
    private static final String CERTIFYING_DOCTOR = "certifying_doctor";

    public static final Iterable<String> DEATH_FIELD_NAMES = Arrays.asList(ORIGINAL_ID, SURNAME, FORENAME, SEX, YEAR_OF_REGISTRATION, REGISTRATION_DISTRICT_NUMBER,
            REGISTRATION_DISTRICT_SUFFIX, ENTRY, DEATH_YEAR, AGE_AT_DEATH, MOTHERS_MAIDEN_SURNAME, CHANGED_SURNAME, CHANGED_FORENAME, CHANGED_DEATH_AGE,
            DATE_OF_BIRTH, OCCUPATION, MARITAL_STATUS, SPOUSES_NAMES, SPOUSES_OCCUPATIONS,   // TODO check fieldname spouses_occupations - wrong in exporter - population project
            DEATH_MONTH, DEATH_DAY,
            PLACE_OF_DEATH, FATHERS_FORENAME, FATHERS_SURNAME, FATHERS_OCCUPATION, FATHER_DECEASED, MOTHERS_FORENAME,
            MOTHERS_SURNAME, CHANGED_MOTHERS_MAIDEN_SURNAME,
            MOTHER_DECEASED, COD_A, COD_B, COD_C, CERTIFYING_DOCTOR, CORRECTED_ENTRY, IMAGE_QUALITY);

    // Marriage fields

    private static final String GROOM_SURNAME = "groom_surname";
    private static final String GROOM_FORENAME = "groom_forename";
    private static final String BRIDE_SURNAME = "bride_surname";
    private static final String BRIDE_FORENAME = "bride_forename";
    private static final String MARRIAGE_YEAR = "marriage_year";
    private static final String CHANGED_GROOM_SURNAME = "changed_groom_surname";
    private static final String CHANGED_GROOM_FORENAME = "changed_groom_forename";
    private static final String CHANGED_BRIDE_SURNAME = "changed_bride_surname";
    private static final String CHANGED_BRIDE_FORENAME = "changed_bride_forename";
    private static final String GROOM_DID_NOT_SIGN = "groom_did_not_sign";
    private static final String BRIDE_DID_NOT_SIGN = "bride_did_not_sign";
    private static final String MARRIAGE_DAY = "marriage_day";
    private static final String MARRIAGE_MONTH = "marriage_month";
    private static final String DENOMINATION = "denomination";
    private static final String GROOM_ADDRESS = "groom_address";
    private static final String GROOM_AGE_OR_DATE_OF_BIRTH = "groom_age_or_date_of_birth";
    private static final String GROOM_OCCUPATION = "groom_occupation";
    private static final String GROOM_MARITAL_STATUS = "groom_marital_status";
    private static final String BRIDE_ADDRESS = "bride_address";
    private static final String BRIDE_AGE_OR_DATE_OF_BIRTH = "bride_age_or_date_of_birth";
    private static final String BRIDE_OCCUPATION = "bride_occupation";
    private static final String BRIDE_MARITAL_STATUS = "bride_marital_status";
    private static final String GROOM_FATHERS_FORENAME = "groom_fathers_forename";
    private static final String GROOM_FATHERS_SURNAME = "groom_fathers_surname";
    private static final String GROOM_FATHER_DECEASED = "groom_father_deceased";
    private static final String GROOM_MOTHERS_FORENAME = "groom_mothers_forename";
    private static final String GROOM_MOTHERS_MAIDEN_SURNAME = "groom_mothers_maiden_surname";
    private static final String GROOM_MOTHER_DECEASED = "groom_mother_deceased";
    private static final String GROOM_FATHERS_OCCUPATION = "groom_fathers_occupation";
    private static final String BRIDE_FATHERS_FORENAME = "bride_fathers_forename";
    private static final String BRIDE_FATHERS_SURNAME = "bride_fathers_surname";
    private static final String BRIDE_FATHER_DECEASED = "bride_father_deceased";
    private static final String BRIDE_MOTHERS_FORENAME = "bride_mothers_forename";
    private static final String BRIDE_MOTHERS_MAIDEN_SURNAME = "bride_mothers_maiden_surname";
    private static final String BRIDE_MOTHER_DECEASED = "bride_mother_deceased";
    private static final String BRIDE_FATHER_OCCUPATION = "bride_father_occupation";

    public static final Iterable<String> MARRIAGE_FIELD_NAMES = Arrays.asList(ORIGINAL_ID, GROOM_SURNAME, GROOM_FORENAME,
            BRIDE_SURNAME, BRIDE_FORENAME, YEAR_OF_REGISTRATION,
            REGISTRATION_DISTRICT_NUMBER, REGISTRATION_DISTRICT_SUFFIX, ENTRY, MARRIAGE_YEAR,
            CHANGED_GROOM_SURNAME, CHANGED_GROOM_FORENAME,
            CHANGED_BRIDE_SURNAME, CHANGED_BRIDE_FORENAME, GROOM_DID_NOT_SIGN,
            BRIDE_DID_NOT_SIGN, MARRIAGE_DAY, MARRIAGE_MONTH, DENOMINATION, GROOM_ADDRESS,
            GROOM_AGE_OR_DATE_OF_BIRTH, GROOM_OCCUPATION,
            GROOM_MARITAL_STATUS, BRIDE_ADDRESS, BRIDE_AGE_OR_DATE_OF_BIRTH,
            BRIDE_OCCUPATION, BRIDE_MARITAL_STATUS, GROOM_FATHERS_FORENAME,
            GROOM_FATHERS_SURNAME, GROOM_FATHER_DECEASED, GROOM_MOTHERS_FORENAME, GROOM_MOTHERS_MAIDEN_SURNAME,
            GROOM_MOTHER_DECEASED, GROOM_FATHERS_OCCUPATION, BRIDE_FATHERS_FORENAME, BRIDE_FATHERS_SURNAME,
            BRIDE_FATHER_DECEASED, BRIDE_MOTHERS_FORENAME, BRIDE_MOTHERS_MAIDEN_SURNAME, BRIDE_MOTHER_DECEASED,
            BRIDE_FATHER_OCCUPATION, CORRECTED_ENTRY, IMAGE_QUALITY);

}

        /*        Fields are as follows:
        *         Ref Field
        *         1. Unique 'Record' Identifier
        *         2. Groom Surname
        *         3. Groom Forename
        *         4. Bride Surname
        *         5. Bride Forename
        *         6. Year of Registration
        *         7. Registration District Number
        *         8. Registration District Suffix
        *         9. Entry
        *         10. Marriage Year
        *         11. Changed Groom Surname
        *         12. Changed Groom Forename
        *         13. Changed Bride Surname
        *         14. Changed Bride Forename
        *         15. Groom did not Sign ('X' or empty)
        *         16. Bride did not Sign ('X' or empty)
        *         17. Marriage Day
        *         18. Marriage Month
        *         19. Denomination
        *         20. Groom’s Address
        *         21. Groom Age or Date of Birth
        *         22. Groom’s Occupation
        *         23. Groom Marital Status
        *         24. Bride’s Address
        *         25. Bride Age or Date of Birth
        *         26. Bride’s Occupation
        *         27. Bride marital status
        *         28. Groom Father’s Forename
        *         29. Groom Father’s Surname ('0' if same as Groom Surname)
        *         30. Groom Father Deceased ('Y' or empty)
        *         31. Groom Mother’s Forename
        *         32. Groom Mother’s Maiden Surname
        *         33. Groom mother Deceased ('Y' or empty)
        *         34. Groom Father Occupation
        *         35. Bride Father’s Forename
        *         36. Bride Father’s Surname ('0' if same as Bride Surname)
        *         37. Bride Father Deceased ('Y' or empty)
        *         38. Bride Mother’s Forename
        *         39. Bride Mother’s Maiden Surname
        *         40. Bride Mother Deceased ('Y' or empty)
        *         41. Bride Father Occupation
        *         42. Corrected Entry ('1', '2', '3' or empty)
        *         43. Image Quality ('1', '2' or empty)
        *         <p/>
        *         <p/>
        *         <p/>
        *         Examples of marriage records:
        *         <p/>
        *         9000001|MCMILLAN|JOHN|MCDONALD|JANET|1855|107|01|15|1855||||||X|20|11|1|MILLHAVEN_OF_URQUHART_CO_INVERNESS|30|TAILOR_(M ASTER)|B|MILLHAVEN_OF_URQUHART_CO_INVERNESS|23|DOMESTIC_SERVANT|S|WILLIAM|0|Y|HELEN|GRANT||TAILOR|JOHN|0|Y|CATH ERINE|CAMERON||FOX_HUNTER|||
        *         9000002|FRASER|DONALD|FRASER|CHRISTINA|1855|107|01|0006|1855|||||||29|11|1|EASTLOCH_OF_INVERNESS|26|MASON_(JOURNEYMAN)| B|DRUMNADROCHIT_OF_URQUHART|25|DOMESTIC_SERVANT|S|ANDREW|0||ELINA|CUMMING|Y|FARMER|ALEXANDER|0|Y|ELIZABETH|C UMMING||CARPENTER|||
        */

