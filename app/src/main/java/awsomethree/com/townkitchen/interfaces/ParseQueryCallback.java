package awsomethree.com.townkitchen.interfaces;

import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.List;

/**
 * Created by smulyono on 3/24/15.
 */

/**
 * Any activity or fragments need to access the callback from specific Parse query needs
 * to implement this
 */
public interface ParseQueryCallback {
    public void parseQueryDone(List<? extends ParseObject> parseObjects, ParseException e,
            int queryCode);
}
