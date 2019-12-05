public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getExtras() != null
                && getIntent().getExtras().getString(ClassConstants.Notification.ACTIVITY_NAME) != null
                && SharedPrefManager.getInstance(this).get(SharedPrefManager.Key.IS_SIGN_IN_COMPLETED,
                false)) {
            // access bundle data
            Bundle bundle = getIntent().getExtras();
            String activity_name = bundle.getString(ClassConstants.Notification.ACTIVITY_NAME);
            String chatRoomId = bundle.getString(ClassConstants.Notification.CHAT_ROOM);
            String chatName = bundle.getString(ClassConstants.Notification.CHAT_NAME);
            String chatType = bundle.getString(ClassConstants.Notification.CHAT_TYPE);
            // resolve activity
            Intent intent = ClassUtility.createIntentUsingClassName(this, activity_name);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // handle chat intent
            if (TextUtils.equals(activity_name, ClassConstants.Notification.CHAT_ACTIVITY)) {
                intent.putExtra(ClassConstants.Bundle.ID, chatRoomId);
                intent.putExtra(ClassConstants.Bundle.NAME, chatName);
                intent.putExtra(ClassConstants.Bundle.TYPE, chatType);
            }
            // task builder
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(intent);
            stackBuilder.startActivities();
            finish();
        } else {
            launchNextScreen();
        }
    }

    private void launchNextScreen() {
        Intent intent;
        // welcome screen shown or not
        if (!SharedPrefManager.getInstance(this).get(SharedPrefManager.Key.IS_BOARDING_COMPLETED, false)) {
            intent = new Intent(SplashActivity.this, WelcomeScreenActivity.class);
        }
        // if sign not completed or access token is null
        else if (!SharedPrefManager.getInstance(this).get(SharedPrefManager.Key.IS_SIGN_IN_COMPLETED, false) ||
                SharedPrefManager.getInstance(this).get(SharedPrefManager.Key.ACCESS_TOKEN, null) == null) {
            intent = new Intent(SplashActivity.this, SignInActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, DashboardActivity.class);
        }
        startActivity(intent);
        finish();
    }
