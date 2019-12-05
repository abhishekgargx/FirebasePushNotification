public class Utility{
    public static Intent createIntentUsingClassName(Context context, String className) {
        if (className == null || className.isEmpty()) {
            return new Intent(context, SplashActivity.class);
        }
        try {
            String activityname =  className;
            Class class_name = Class.forName(activityname);
            return new Intent(context, class_name);
        } catch (Exception e) {
            return new Intent(context, SplashActivity.class);
        }
    }

}
