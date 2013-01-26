package org.christiankakesa.applications.java.shelltaskpool;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Calendar;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class AppInfoTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testAppInfo() {
        assertNotNull(AppInfo.APP_NAME);
        assertNotNull(AppInfo.AUTHOR_NAME);
        assertNotNull(AppInfo.AUTHOR_EMAIL);
        assertNotNull(AppInfo.APP_COPYRIGHT);
        assertTrue(AppInfo.APP_COPYRIGHT.endsWith(String.valueOf(Calendar.getInstance().get(Calendar.YEAR))));
    }

    @Test
    public void AppInfoConstructorTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        /**
         * @see http://www.stupidplebs.com/testing-private-constructors
         */
        // get the constructor that takes no parameters
        Constructor<?> constructor = AppInfo.class.getDeclaredConstructor();
        // the modifiers int can tell us metadata about the constructor
        int constructorModifiers = constructor.getModifiers();
        // but we're only interested in knowing that it's private
        assertThat(Modifier.isPrivate(constructorModifiers), is(true));
        // constructor is private so we first have to make it accessible
        constructor.setAccessible(true);
        // now construct an instance
        constructor.newInstance();
    }
}
