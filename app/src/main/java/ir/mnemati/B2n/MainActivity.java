package ir.mnemati.B2n;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.jsoup.Jsoup;
import org.jsoup.Connection;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    TextView resultTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* ***** Define Widgets ***** */
        EditText inputUrl = findViewById(R.id.et_input);
        Button pasteBtn = findViewById(R.id.btn_paste_url);
        Button shortLinkBtn = findViewById(R.id.btn_short_link);
        Button copyBtn = findViewById(R.id.btn_copy);
        Button shareBtn = findViewById(R.id.btn_share);
        Button clearBtn = findViewById(R.id.btn_clear);
        resultTextView = findViewById(R.id.tv_result);

        /* ***** Short Link Button ***** */
        shortLinkBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String myUrl = inputUrl.getText().toString();
                new JsoupParseTask().execute(myUrl);
                copyBtn.setFocusable(true);
                copyBtn.setFocusableInTouchMode(true);
                copyBtn.requestFocus();

            }
        });

        /* ***** Pate Button ***** */
        pasteBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                String pasteData = "";
                if(clipboard.hasPrimaryClip()){
                    ClipData pData = clipboard.getPrimaryClip();
                    ClipData.Item item = pData.getItemAt(0);
                    inputUrl.setText(item.getText().toString());
                    Context cnt = getApplicationContext();
                    Toast.makeText(cnt, "Paste Done", Toast.LENGTH_SHORT).show();
                }
                else {
                    Context cnt = getApplicationContext();
                    Toast.makeText(cnt, "Nothing exist!", Toast.LENGTH_SHORT).show();
                }



            }
        });

        /* ***** Clear Button ***** */
        clearBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                inputUrl.setText("");
                resultTextView.setText("");
                inputUrl.requestFocus();
                Context cnt = getApplicationContext();
                Toast.makeText(cnt, "Clear", Toast.LENGTH_SHORT).show();
            }
        });

        /* ***** Copy Button ***** */
        copyBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("b2n link", resultTextView.getText().toString());
                Context cnt = getApplicationContext();
                Toast.makeText(cnt, "Copy Done!", Toast.LENGTH_SHORT).show();
            }
        });

        /* ***** Share Button ***** */
        shareBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String s = resultTextView.getText().toString();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, s);
                startActivity(Intent.createChooser(sharingIntent, "Share text via"));
            }
        });
    }

    class JsoupParseTask extends AsyncTask<String, Void, Document> {
        @Override
        protected Document doInBackground(String... urls) {

            String getUrl = urls[0];
            Document document = null;
            try {
                Connection.Response response =
                        Jsoup.connect("https://b2n.ir/create.php")
                                .method(Connection.Method.POST)
                                .data("url", getUrl)
                                .data("custom", "")
                                //.followRedirects(true)
                                .execute();
                document = response.parse();

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            return document;
        }

        @Override
        protected void onPostExecute(Document doc) {
            // execution of result here
            Element frm = doc.getElementById("website");
            String str = frm.val();
            resultTextView.setText(str);
        }

    }
}
