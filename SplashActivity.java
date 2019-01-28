public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
       
        // when app is in background or swiped from recent , handle custom data or intents or actions like below
        // below code should always in your app launcher activity , onCreate method , in my case it is SplashActivity 
        if (getIntent().getExtras() != null) {
             Bundle bundle = getIntent().getExtras();
                    if(bundle != null) { 
                        // activity_name is key name of custom data i sending
                       String  value = bundle.getString("activity_name");
                         if( value != null){
                             // perform any condition you want to execute based on value
                             // i am starting google.com in web browser
                          switch(value){
                                case "google":{
                                  startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.google.com")));
                                  break;
                                 }
                                 default:{
                                    startActivity(new Intent(this, MainActivity.class) );
                                    break;
                               	}
                              }
                        }else{
                           startActivity(new Intent(this, MainActivity.class) );
                         }
                    }
                    else{
                       startActivity(new Intent(this, MainActivity.class) );
                   }
        }
        else{
           startActivity(new Intent(this, MainActivity.class) );
       }
      
    }
}
