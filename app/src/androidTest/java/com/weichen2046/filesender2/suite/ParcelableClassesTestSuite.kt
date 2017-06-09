package com.weichen2046.filesender2.suite

import com.weichen2046.filesender2.DesktopClassTest
import com.weichen2046.filesender2.PhoneClassTest
import com.weichen2046.filesender2.RemoteDeviceWrapperTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * Created by chenwei on 6/9/17.
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
        DesktopClassTest::class,
        PhoneClassTest::class,
        RemoteDeviceWrapperTest::class
)
class ParcelableClassesTestSuite {
}