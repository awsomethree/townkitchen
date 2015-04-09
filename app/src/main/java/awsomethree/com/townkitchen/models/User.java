package awsomethree.com.townkitchen.models;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.parse.ParseTwitterUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import awsomethree.com.townkitchen.R;

/**
 * Created by smulyono on 4/8/15.
 */
public class User {
    private View mainView;
    public User(View v){
        this.mainView = v;
        new PopulateTwitterProfile().execute();
    }

    public static void populateTwitterProfileImage(View v){
        User user = new User(v);
    }

    class PopulateTwitterProfile extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... params) {
            // 1. do Twitter client api to get the twitter profile
            HttpClient client = new DefaultHttpClient();
            HttpGet verifyGet = new HttpGet(
                    "https://api.twitter.com/1.1/account/verify_credentials.json");
            ParseTwitterUtils.getTwitter().signRequest(verifyGet);
            String strResponse = "{}";
            try {
                HttpResponse response = client.execute(verifyGet);
                strResponse = EntityUtils.toString(response.getEntity());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return strResponse;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            // expecting data back as JSON
            String name = "-";
            String profileImageUrl = "";
            try {
                JSONObject responseJSON = new JSONObject(aVoid);
                // get name
                name = responseJSON.getString("name");
                // get profileImageURL
                profileImageUrl = responseJSON.getString("profile_image_url");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            TextView tv = (TextView) mainView.findViewById(R.id.tvProfileName);
            tv.setText(name);

            if (!profileImageUrl.isEmpty()) {
                ImageView iv = (ImageView) mainView.findViewById(R.id.ivProfileImg);
                // make rounded image transformation
                Transformation transformation = new RoundedTransformationBuilder()
                        .borderWidthDp(0)
                        .cornerRadiusDp(5)
                        .oval(false)
                        .build();
                // change the image
                iv.setImageResource(R.mipmap.ic_nopic);
                Picasso.with(mainView.getContext())
                        .load(profileImageUrl)
                        .resize(40, 40)
                        .transform(transformation)
                        .into(iv);
            }
        }
    }

}
