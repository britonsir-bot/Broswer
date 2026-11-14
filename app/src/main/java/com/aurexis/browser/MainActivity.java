package com.aurexis.browser;

import android.app.*;
import android.os.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.content.*;
import android.net.Uri;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.*;
import android.text.*;
import org.json.*;
import java.net.*;
import java.io.*;
import java.util.*;
import org.mozilla.geckoview.*;

public class MainActivity extends Activity {
    private static final String HOME="https://duckduckgo.com/";
    private static final int BG=0xFF0B1220, BAR=0xFF111B2D, PANEL=0xFF17243A, BLUE=0xFF76A9FF, TEXT=0xFFEAF1FC, MUTED=0xFF9BAEC9;
    private GeckoRuntime runtime;
    private WebExtensionController extensions;
    private LinearLayout root, toolbar, tabStrip;
    private EditText address;
    private FrameLayout content;
    private TextView status;
    private final ArrayList<Tab> tabs=new ArrayList<>();
    private int active=0;
    private static class Tab { GeckoSession session; GeckoView view; String title="New tab"; String uri=HOME; }
    private int dp(float v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}
    private TextView text(String s,int size,int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);t.setGravity(Gravity.CENTER);return t;}
    private GradientDrawable shape(int color,int radius,int stroke){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));if(stroke!=0)d.setStroke(dp(1),stroke);return d;}
    private Button button(String label,Runnable run){Button b=new Button(this);b.setText(label);b.setTextColor(TEXT);b.setTextSize(17);b.setAllCaps(false);b.setMinWidth(dp(42));b.setMinimumWidth(dp(42));b.setMinHeight(dp(42));b.setPadding(dp(8),0,dp(8),0);b.setBackground(shape(PANEL,12,0xFF304663));b.setOnClickListener(v->run.run());return b;}
    @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(BAR);getWindow().setNavigationBarColor(BG);getWindow().getDecorView().setSystemUiVisibility(0);
        runtime=GeckoRuntime.create(this);extensions=runtime.getWebExtensionController();buildUI();newTab(HOME);
        String incoming=getIntent().getDataString();if(incoming!=null)load(incoming);
    }
    private void buildUI(){
        root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(BG);setContentView(root);
        LinearLayout brand=new LinearLayout(this);brand.setGravity(Gravity.CENTER_VERTICAL);brand.setPadding(dp(14),dp(8),dp(14),dp(5));brand.setBackgroundColor(BAR);
        TextView logo=text("◈",24,BLUE);brand.addView(logo,new LinearLayout.LayoutParams(dp(34),dp(36)));
        TextView name=text("AUREXIS",15,TEXT);name.setTypeface(null,Typeface.BOLD);name.setGravity(Gravity.CENTER_VERTICAL);brand.addView(name);
        TextView sub=text("  BROWSER 2",9,MUTED);brand.addView(sub);View spacer=new View(this);brand.addView(spacer,new LinearLayout.LayoutParams(0,1,1));
        TextView store=text("▦  EXTENSIONS",10,BLUE);store.setPadding(dp(10),dp(9),dp(10),dp(9));store.setBackground(shape(PANEL,10,0xFF304663));store.setOnClickListener(v->showStore());brand.addView(store);root.addView(brand);
        toolbar=new LinearLayout(this);toolbar.setGravity(Gravity.CENTER_VERTICAL);toolbar.setPadding(dp(7),dp(7),dp(7),dp(7));toolbar.setBackgroundColor(BAR);
        toolbar.addView(button("‹",()->goBack()));toolbar.addView(button("›",()->goForward()));
        Button reload=button("↻",()->{if(current()!=null)current().session.reload();});toolbar.addView(reload);
        address=new EditText(this);address.setSingleLine(true);address.setTextSize(13);address.setTextColor(TEXT);address.setHintTextColor(MUTED);address.setHint("Search or enter address");address.setPadding(dp(12),0,dp(12),0);address.setSelectAllOnFocus(true);address.setBackground(shape(0xFF0A1424,13,0xFF344D70));
        LinearLayout.LayoutParams ep=new LinearLayout.LayoutParams(0,dp(44),1);ep.setMargins(dp(5),0,dp(5),0);toolbar.addView(address,ep);
        address.setImeOptions(EditorInfo.IME_ACTION_GO);address.setOnEditorActionListener((v,a,event)->{navigate();return true;});
        toolbar.addView(button("＋",()->newTab(HOME)));toolbar.addView(button("⋮",()->menu()));root.addView(toolbar);
        tabStrip=new LinearLayout(this);tabStrip.setOrientation(LinearLayout.HORIZONTAL);tabStrip.setPadding(dp(5),dp(4),dp(5),dp(4));tabStrip.setBackgroundColor(0xFF0E192A);root.addView(tabStrip);
        content=new FrameLayout(this);root.addView(content,new LinearLayout.LayoutParams(-1,0,1));
        status=text("Ready  ·  Gecko engine",10,MUTED);status.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);status.setPadding(dp(12),dp(6),dp(12),dp(6));status.setBackgroundColor(BAR);root.addView(status);
    }
    private Tab current(){return tabs.size()>active?tabs.get(active):null;}
    private void newTab(String url){
        Tab t=new Tab();t.session=new GeckoSession();t.session.open(runtime);t.view=new GeckoView(this);t.view.setSession(t.session);
        t.session.setNavigationDelegate(new GeckoSession.NavigationDelegate(){@Override public void onLocationChange(GeckoSession s,String u,java.util.List<GeckoSession.PermissionDelegate.ContentPermission> perms,Boolean gesture){t.uri=u==null?"":u;if(tabs.indexOf(t)==active)address.setText(t.uri);}});
        t.session.setProgressDelegate(new GeckoSession.ProgressDelegate(){@Override public void onPageStart(GeckoSession s,String u){if(tabs.indexOf(t)==active)status.setText("Loading…");}@Override public void onPageStop(GeckoSession s,boolean ok){if(tabs.indexOf(t)==active)status.setText(ok?"Ready  ·  Gecko engine":"Page load finished with errors");}});
        t.session.setContentDelegate(new GeckoSession.ContentDelegate(){@Override public void onTitleChange(GeckoSession s,String title){t.title=title==null?"Tab":title;refreshTabs();}});
        if(current()!=null)current().session.setActive(false);tabs.add(t);active=tabs.size()-1;t.session.setActive(true);content.removeAllViews();content.addView(t.view);refreshTabs();t.session.loadUri(url);
    }
    private void refreshTabs(){tabStrip.removeAllViews();for(int i=0;i<tabs.size();i++){final int ix=i;Tab t=tabs.get(i);TextView b=text((i==active?"● ":"")+shorten(t.title,16)+"  ×",11,i==active?TEXT:MUTED);b.setPadding(dp(10),dp(8),dp(10),dp(8));b.setBackground(shape(i==active?PANEL:BG,9,i==active?BLUE:0));b.setOnClickListener(v->{if(tabs.size()>ix){if(current()!=null)current().session.setActive(false);active=ix;current().session.setActive(true);content.removeAllViews();content.addView(current().view);address.setText(current().uri);refreshTabs();}});b.setOnLongClickListener(v->{closeTab(ix);return true;});LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-2,dp(34));p.setMargins(dp(3),0,dp(3),0);tabStrip.addView(b,p);}}
    private String shorten(String s,int n){return s.length()>n?s.substring(0,n-1)+"…":s;}
    private void closeTab(int i){if(i<0||i>=tabs.size())return;Tab t=tabs.remove(i);t.session.close();if(tabs.isEmpty()){newTab(HOME);return;}active=Math.min(active,tabs.size()-1);current().session.setActive(true);content.removeAllViews();content.addView(current().view);address.setText(current().uri);refreshTabs();}
    private void navigate(){String s=address.getText().toString().trim();if(s.isEmpty())return;String u;if(s.matches("(?i)^https?://.*"))u=s;else if(s.contains(" ")||!s.contains("."))u="https://duckduckgo.com/?q="+Uri.encode(s);else u="https://"+s;load(u);((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(address.getWindowToken(),0);current().view.requestFocus();}
    private void load(String u){if(current()==null)return;current().uri=u;address.setText(u);current().session.loadUri(u);}
    private void goBack(){if(current()!=null)current().session.goBack();}
    private void goForward(){if(current()!=null)current().session.goForward();}
    private void menu(){String[] items={"New tab","Extensions store","Bookmarks (coming soon)","Downloads (coming soon)","Settings","Close current tab"};new AlertDialog.Builder(this).setTitle("AUREXIS Browser").setItems(items,(d,w)->{if(w==0)newTab(HOME);if(w==1)showStore();if(w==4)settings();if(w==5)closeTab(active);}).show();}
    private void settings(){new AlertDialog.Builder(this).setTitle("Browser settings").setMessage("Engine: Mozilla GeckoView\nABI: armeabi-v7a\nExtension source: addons.mozilla.org\nHardware acceleration: Android default\nMemory note: this device is below Mozilla's 2 GB recommendation.").setPositiveButton("OK",null).show();}
    private void showStore(){
        LinearLayout box=new LinearLayout(this);box.setPadding(dp(12),dp(8),dp(12),dp(8));box.setOrientation(LinearLayout.VERTICAL);box.setBackgroundColor(BG);
        EditText q=new EditText(this);q.setSingleLine(true);q.setHint("Search open-source Firefox add-ons");q.setTextColor(TEXT);q.setHintTextColor(MUTED);q.setBackground(shape(PANEL,10,0xFF344D70));box.addView(q);
        TextView info=text("Official Mozilla Add-ons catalogue · signed extensions only",10,MUTED);box.addView(info);
        ScrollView scroll=new ScrollView(this);LinearLayout results=new LinearLayout(this);results.setOrientation(LinearLayout.VERTICAL);scroll.addView(results);box.addView(scroll,new LinearLayout.LayoutParams(-1,dp(360)));
        AlertDialog dialog=new AlertDialog.Builder(this).setTitle("Extension Store").setView(box).setNegativeButton("Close",null).create();
        q.setImeOptions(EditorInfo.IME_ACTION_SEARCH);q.setOnEditorActionListener((v,a,e)->{searchAddons(q.getText().toString(),results,info);return true;});
        dialog.setOnShowListener(v->searchAddons("recommended",results,info));dialog.show();
    }
    private void searchAddons(String query,LinearLayout target,TextView info){
        target.removeAllViews();info.setText("Searching Mozilla Add-ons…");
        new AsyncTask<Void,Void,JSONArray>(){
            String error=null;
            protected JSONArray doInBackground(Void...v){try{String url="https://addons.mozilla.org/api/v5/addons/search/?q="+URLEncoder.encode(query,"UTF-8")+"&app=android&lang=en-US&page_size=12";HttpURLConnection c=(HttpURLConnection)new URL(url).openConnection();c.setConnectTimeout(12000);c.setReadTimeout(15000);c.setRequestProperty("User-Agent","AUREXIS-Browser/2");InputStream in=c.getInputStream();ByteArrayOutputStream out=new ByteArrayOutputStream();byte[]buf=new byte[8192];int n;while((n=in.read(buf))>0)out.write(buf,0,n);c.disconnect();return new JSONObject(out.toString("UTF-8")).optJSONArray("results");}catch(Exception e){error=e.toString();return null;}}
            protected void onPostExecute(JSONArray arr){target.removeAllViews();if(arr==null){info.setText("Catalogue unavailable: "+error);return;}info.setText(arr.length()+" results · Mozilla Add-ons");for(int i=0;i<arr.length();i++){JSONObject item=arr.optJSONObject(i);if(item==null)continue;String name=item.optString("name","Extension"),desc=item.optString("summary",""),slug=item.optString("slug","");JSONObject ver=item.optJSONObject("current_version");JSONArray f=ver==null?null:ver.optJSONArray("files");JSONObject file=f==null||f.length()==0?null:f.optJSONObject(0);String xpi=file==null?"":file.optString("url","");String id=item.optString("guid","");LinearLayout card=new LinearLayout(MainActivity.this);card.setOrientation(LinearLayout.VERTICAL);card.setPadding(dp(10),dp(9),dp(10),dp(9));card.setBackground(shape(PANEL,12,0));TextView title=text(name,14,TEXT);title.setGravity(Gravity.LEFT);title.setTypeface(null,Typeface.BOLD);card.addView(title);TextView summary=text(desc,11,MUTED);summary.setGravity(Gravity.LEFT);card.addView(summary);Button install=button("Install",()->installAddon(xpi,name));install.setEnabled(!xpi.isEmpty());LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(-2,dp(40));bp.topMargin=dp(5);card.addView(install,bp);LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2);cp.setMargins(0,dp(5),0,dp(5));target.addView(card,cp);}}
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    private void installAddon(String url,String name){if(url==null||url.isEmpty())return;new AlertDialog.Builder(this).setTitle("Install "+name+"?").setMessage("This extension is provided by Mozilla Add-ons. Review the permissions requested during installation.").setNegativeButton("Cancel",null).setPositiveButton("Install",(d,w)->{status.setText("Installing extension…");extensions.install(url,WebExtensionController.INSTALLATION_METHOD_MANAGER).accept(ext->{runOnUiThread(()->new AlertDialog.Builder(this).setMessage("Installed: "+name).setPositiveButton("OK",null).show());},err->{runOnUiThread(()->new AlertDialog.Builder(this).setTitle("Installation failed").setMessage(err.toString()).setPositiveButton("OK",null).show());});}).show();}
    @Override public void onBackPressed(){if(current()!=null){goBack();}else super.onBackPressed();}
    @Override protected void onDestroy(){for(Tab t:tabs)t.session.close();tabs.clear();super.onDestroy();}
}
