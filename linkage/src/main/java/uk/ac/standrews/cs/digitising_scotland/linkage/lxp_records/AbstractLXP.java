package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records;

import uk.ac.standrews.cs.storr.impl.LXP;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IRepository;

/**
 * Created by al on 14/10/2014.
 */
public abstract class AbstractLXP extends LXP {

    public AbstractLXP() {

        super();
    }

    public AbstractLXP(long object_id, JSONReader reader, IRepository repository, IBucket bucket) throws PersistentObjectException, IllegalKeyException {

        super(object_id, reader, repository, bucket);
    }

    protected String cleanDate(String day, String month, String year) {

        return cleanDay(day) + DATE_SEPARATOR + cleanMonth(month) + DATE_SEPARATOR + cleanYear(year);
    }

    private String cleanDay(final String day) {

        if (notGiven(day)) {
            return BLANK_DAY;
        }

        try {
            String d = String.valueOf(Integer.parseInt(day));

            if (d.length() == 1) {
                return "0" + d;
            }
            return d;
        }
        catch (NumberFormatException e) {
            return BLANK_DAY;
        }
    }

    private String cleanMonth(final String month) {

        if (notGiven(month)) {
            return BLANK_MONTH;
        }

        if (month.length() > 3) {
            return month.substring(0, 3);
        }
        return month;
    }

    private String cleanYear(final String year) {

        if (notGiven(year)) {
            return BLANK_YEAR;
        }

        try {
            int i = Integer.parseInt(year);
            if (i > 10) {
                i += 1800;
            }
            else {
                i += 1900;
            }
            return String.valueOf(i);
        }
        catch (NumberFormatException e) {
            return BLANK_YEAR;
        }
    }

    private boolean notGiven(final String field) {

        return field.equals("") || field.equals("na") || field.equals("ng");
    }

    private static final String DATE_SEPARATOR = "/";

    private static final String BLANK_DAY = "--";
    private static final String BLANK_MONTH = "---";
    private static final String BLANK_YEAR = "----";
}
