//package info.androidhive.navigationdrawer.unit_tests;
//
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
////import org.junit.Before;
////import org.junit.Test;
////import org.junit.runner.RunWith;
////import org.robolectric.Robolectric;
////import org.robolectric.RobolectricTestRunner;
////import org.robolectric.annotation.Config;
//
//import info.androidhive.navigationdrawer.R;
//
//import static junit.framework.Assert.assertEquals;
//import static org.junit.Assert.assertEquals;
//
//@RunWith(RobolectricTestRunner.class)
//@Config(constants = BuildConfig.class)
//public class RobolectricActivityTest {
//
//    private RobolectricActivity activity;
//
//    @Before
//    public void setUp() throws Exception {
//        activity = Robolectric.buildActivity(RobolectricActivity.class)
//                .create()
//                .resume()
//                .get();
//    }
//
//    @Test
//    public void shouldHaveDefaultMargin() throws Exception {
//        TextView textView = (TextView) activity.findViewById(R.id.hello_textview);
//        int bottomMargin = ((LinearLayout.LayoutParams) textView.getLayoutParams()).bottomMargin;
//        assertEquals(5, bottomMargin);
//        int topMargin = ((LinearLayout.LayoutParams) textView.getLayoutParams()).topMargin;
//        assertEquals(5, topMargin);
//        int rightMargin = ((LinearLayout.LayoutParams) textView.getLayoutParams()).rightMargin;
//        assertEquals(10, rightMargin);
//        int leftMargin = ((LinearLayout.LayoutParams) textView.getLayoutParams()).leftMargin;
//        assertEquals(10, leftMargin);
//    }
//}