package com.fast.radio;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {
    private final RadioSearchEngine searchEngine = new RadioSearchEngine();
    private final IranRadioRepository iranRepo = new IranRadioRepository();
    private RadioPlayer player;
    private LinearLayout searchResults;
    private EditText name;
    private Spinner country;
    private LinearLayout iranColumn;
    private LinearLayout firstColumn;
    private TextView iranCount;

    private final List<String> builtInStations = Arrays.asList(
            "BBC فارسی", "Iran International", "Afghanistan International", "i24NEWS Hebrew",
            "Radio Sputnik فارسی", "RT", "NHK", "China Radio", "Thailand Radio",
            "Malaysia Radio", "Indonesia Radio", "Philippines Radio", "Vietnam Radio",
            "Myanmar Radio", "CNN News"
    );

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        player = new RadioPlayer(this);
        buildUi();
        fillBuiltInColumn();
        loadIranColumn();
    }

    private TextView header(String text) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextSize(15);
        t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setGravity(Gravity.CENTER);
        t.setPadding(4, 6, 4, 6);
        t.setTextColor(Color.WHITE);
        t.setBackgroundColor(Color.rgb(35, 65, 95));
        return t;
    }

    private void buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(8,8,8,8);

        TextView title = new TextView(this);
        title.setText("Fast Radio"); title.setTextSize(21); title.setGravity(Gravity.CENTER);
        root.addView(title, new LinearLayout.LayoutParams(-1,56));

        // Two fixed-size, side-by-side station windows. No resize handles.
        LinearLayout lists = new LinearLayout(this);
        lists.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout firstBox = new LinearLayout(this); firstBox.setOrientation(LinearLayout.VERTICAL);
        firstBox.addView(header("لیست اول — رادیوهای داخلی Fast Radio"), new LinearLayout.LayoutParams(-1,52));
        ScrollView firstScroll = new ScrollView(this);
        firstColumn = new LinearLayout(this); firstColumn.setOrientation(LinearLayout.VERTICAL);
        firstScroll.addView(firstColumn);
        firstBox.addView(firstScroll, new LinearLayout.LayoutParams(-1,0,1));

        LinearLayout secondBox = new LinearLayout(this); secondBox.setOrientation(LinearLayout.VERTICAL);
        LinearLayout secondHeader = new LinearLayout(this); secondHeader.setGravity(Gravity.CENTER_VERTICAL);
        TextView h = header("لیست دوم — Iran Radio"); secondHeader.addView(h, new LinearLayout.LayoutParams(0,52,1));
        iranCount = new TextView(this); iranCount.setText("0"); iranCount.setTextColor(Color.WHITE); iranCount.setGravity(Gravity.CENTER); iranCount.setBackgroundColor(Color.rgb(35,65,95));
        secondHeader.addView(iranCount, new LinearLayout.LayoutParams(45,52));
        secondBox.addView(secondHeader);
        ScrollView secondScroll = new ScrollView(this);
        iranColumn = new LinearLayout(this); iranColumn.setOrientation(LinearLayout.VERTICAL);
        secondScroll.addView(iranColumn);
        secondBox.addView(secondScroll, new LinearLayout.LayoutParams(-1,0,1));

        lists.addView(firstBox, new LinearLayout.LayoutParams(0,330,1));
        lists.addView(secondBox, new LinearLayout.LayoutParams(0,330,1));
        root.addView(lists);

        LinearLayout playbackRow = new LinearLayout(this);
        playbackRow.setGravity(Gravity.CENTER);
        Button playButton = new Button(this); playButton.setText("▶ پخش"); playButton.setAllCaps(false);
        Button recordButton = new Button(this); recordButton.setText("● ضبط"); recordButton.setAllCaps(false);
        int oneCmDp = (int)(getResources().getDisplayMetrics().density * 38 + 0.5f);
        playbackRow.addView(playButton, new LinearLayout.LayoutParams(oneCmDp, oneCmDp));
        playbackRow.addView(recordButton, new LinearLayout.LayoutParams(oneCmDp, oneCmDp));
        root.addView(playbackRow);

        LinearLayout info = new LinearLayout(this); info.setOrientation(LinearLayout.VERTICAL);
        TextView infoTitle = header("اطلاعات / تذکرات رادیو");
        TextView infoText = new TextView(this);
        infoText.setText("وضعیت موتور پخش، گیرایی، Buffering و خطاهای ایستگاه در اینجا نمایش داده می‌شود.");
        infoText.setPadding(8,8,8,8);
        info.addView(infoTitle, new LinearLayout.LayoutParams(-1, 44));
        info.addView(infoText, new LinearLayout.LayoutParams(-1, 52));
        root.addView(info);

        name = new EditText(this); name.setHint("نام رادیو یا کلمه جستجو");
        root.addView(name, new LinearLayout.LayoutParams(-1,55));
        country = new Spinner(this);
        String[] countries={"همه کشورها","IR","US","GB","FR","DE","CA","JP","CN","RU","TR","AE","AF","IN"};
        country.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, countries));
        root.addView(country,new LinearLayout.LayoutParams(-1,55));

        LinearLayout buttons=new LinearLayout(this);
        Button search=new Button(this); search.setText("جستجوی جهانی");
        Button iran=new Button(this); iran.setText("بارگذاری Iran Radio");
        buttons.addView(search,new LinearLayout.LayoutParams(0,55,1));
        buttons.addView(iran,new LinearLayout.LayoutParams(0,55,1)); root.addView(buttons);

        TextView hint=new TextView(this);
        hint.setText("جستجو: کشور • زبان • ژانر • کیفیت • HTTPS\nفهرست‌های ثابت بالا جدا از جستجوی جهانی هستند.");
        root.addView(hint);
        ScrollView sv=new ScrollView(this); searchResults=new LinearLayout(this); searchResults.setOrientation(LinearLayout.VERTICAL); sv.addView(searchResults);
        root.addView(sv,new LinearLayout.LayoutParams(-1,0,1));

        search.setOnClickListener(v -> doSearch());
        iran.setOnClickListener(v -> loadIranColumn());
        setContentView(root);
    }

    private Button stationButton(String label, final RadioStation station) {
        Button b = new Button(this); b.setText(label); b.setAllCaps(false); b.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        b.setTextSize(12);
        b.setOnClickListener(v -> { player.play(station.url); Toast.makeText(this,"پخش: "+station.name,Toast.LENGTH_SHORT).show(); });
        return b;
    }

    private void fillBuiltInColumn() {
        firstColumn.removeAllViews();
        for (String s : builtInStations) {
            TextView row = new TextView(this); row.setText("• " + s); row.setTextSize(13); row.setPadding(6,7,6,7); row.setGravity(Gravity.RIGHT);
            firstColumn.addView(row, new LinearLayout.LayoutParams(-1,48));
        }
    }

    private void loadIranColumn(){
        iranColumn.removeAllViews();
        TextView loading=new TextView(this); loading.setText("در حال دریافت..."); loading.setGravity(Gravity.CENTER); iranColumn.addView(loading);
        iranRepo.load(new IranRadioRepository.Callback(){
            public void onResult(List<RadioStation>s){
                iranColumn.removeAllViews(); iranCount.setText(String.valueOf(s.size()));
                for(RadioStation st:s) iranColumn.addView(stationButton(st.name,st),new LinearLayout.LayoutParams(-1,48));
            }
            public void onError(String m){ iranColumn.removeAllViews(); iranCount.setText("0"); TextView e=new TextView(MainActivity.this); e.setText("خطا: "+m); iranColumn.addView(e); }
        });
    }

    private void doSearch(){
        searchResults.removeAllViews(); TextView loading=new TextView(this); loading.setText("در حال جستجو..."); searchResults.addView(loading);
        String cc=country.getSelectedItemPosition()==0?"":country.getSelectedItem().toString();
        searchEngine.search(name.getText().toString(),cc,"","",0,0,false,new RadioSearchEngine.Callback(){
            public void onResult(List<RadioStation> s){showSearch(s);} public void onError(String m){showMessage(m);}
        });
    }
    private void showSearch(List<RadioStation> stations){
        searchResults.removeAllViews(); TextView c=new TextView(this); c.setText("نتایج جهانی: "+stations.size()); searchResults.addView(c);
        for(RadioStation s:stations) searchResults.addView(stationButton(s.name+" — "+s.country+" — "+s.tags,s),new LinearLayout.LayoutParams(-1,58));
    }
    private void showMessage(String m){searchResults.removeAllViews(); TextView t=new TextView(this); t.setText(m); searchResults.addView(t);}
    @Override protected void onDestroy(){ if(player!=null) player.release(); super.onDestroy(); }
}
