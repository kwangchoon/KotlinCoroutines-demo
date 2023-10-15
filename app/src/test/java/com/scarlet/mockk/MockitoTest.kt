package com.scarlet.mockk

import com.google.common.truth.Truth.assertThat
import com.scarlet.mockk.commons.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.mockito.*
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.atMostOnce
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.exceptions.misusing.InvalidUseOfMatchersException
import org.mockito.kotlin.*
import java.lang.IndexOutOfBoundsException
import java.util.ArrayList

class MockitoTest {

    @Mock
    lateinit var car1: Car

    @Spy
    var car2 = Car("Ford", false, 2021)

    /**
     * Create a mock
     */

    @Before
    fun init() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `Mockito - mock creation`() {
        val mockedFile = mock<Path>()

        val fileName = mockedFile.fileName() // does nothing
        assertThat(fileName).isNull()
    }

    @Test
    fun `Mockito - lenient mock`() {
        // Arrange (Given)
        val mockReader = mock<Path>()

        // Act (When)
        val text = mockReader.readText()

        // Assert (Then)
        assertThat(text).isNull()
        verify(mockReader).readText()
    }

    /**
     * When and do*
     */

    @Test
    fun `Mockito - when - thenReturn`() {
        val mockedFile = mock<Path>()

        whenever(mockedFile.readText()).thenReturn("hello world")
    }

    @Test
    fun `Mockito - thenReturn - doReturn`() {
        val mockedFile = mock<Path> {
            on { readText() } doReturn "hello world"
        }

        doReturn("hello world").whenever(mockedFile).readText()
    }

    @Test
    fun `Mockito - theThrow - doThrow`() {
        val mockedFile = mock<Path>()

        whenever(mockedFile.readText()).thenThrow(RuntimeException())
        doThrow(RuntimeException::class).whenever(mockedFile.readText())
    }

    @Test
    fun `Mockito - thenAnswer - doAnswer`() {
        val mockedFile = mock<Path> {
            on { writeText(anyString()) } doAnswer { invocation ->
                println("called with arguments: " + invocation.arguments.joinToString())
            }
        }

        whenever(mockedFile.writeText(anyString())).then { invocation ->
            println("called with arguments: " + invocation.arguments.joinToString())
        }

        doAnswer { invocation ->
            println("called with arguments: " + invocation.getArgument(0))
        }.`when`(mockedFile).writeText(anyString())
    }

    @Test
    fun `Mockito - doNothing for void method`() {
        val mockedFile = mock<Path>()

        doNothing().whenever(mockedFile).writeText(anyString())
    }

    /**
     * Consecutive calls
     */

    @Test
    fun `Mockito - consecutive calls`() {
        val mockedFile = mock<Path> {
            on { readText() } doReturn "read 1" doReturn "read 2" doReturn "read 3"
        }

        // Chain multiple calls
        whenever(mockedFile.readText()).thenReturn("read 1").thenReturn("read 2")
            .thenReturn("read 3")

        // Shorthand
        whenever(mockedFile.readText()).thenReturn("read 1", "read 2", "read 3")

        doReturn("read 1", "read 2", "read 3").whenever(mockedFile).readText()

        // Use different answer types
        whenever(mockedFile.readText())
            .thenReturn("successful read")
            .thenThrow(RuntimeException())
    }

    /**
     * `eq` and `anyXXX`
     */

    /**
     * By default, Mockito verifies argument values by using the equals() method,
     * which corresponds to == in Kotlin.
     * However, once argument matchers like any() are used, then the eq argument
     * matcher must be used for literal values.
     */
    @Test(expected = InvalidUseOfMatchersException::class)
    fun `Mockito - eq`() {
        val mockCodec = mock<PasswordCodec> {
            on { encode("hello", "RSA") } doReturn "olleh"
            on { encode(anyString(), anyString()) }.thenReturn("olleh")
        }

        // throws exception
        whenever(mockCodec.encode("hello", anyString())).thenReturn("olleh")
    }

    /**
     * Verify
     */

    @Test
    fun `Mockito - verify`() {
        val mockedFile = mock<Path>()

        mockedFile.readText()

        verify(mockedFile).readText()
    }

    /**
     * Verification Mode:
     *
     * Mockito lets extra arguments such as never() be passed to verify in the
     * second parameter, all of which implement a VerificationMode interface.
     * MockK has equivalents for these modes as keyword arguments in verify.
     */

    @Test
    @Ignore
    fun `Mockito - verification mode`() {
        val mockedFile = mock<Path>()

        verify(mockedFile, never()).writeText("hello")
        verify(mockedFile, atLeast(3)).readText()
        verify(mockedFile, atLeastOnce()).writeText("hello")
        verify(mockedFile, atMost(3)).readText()
        verify(mockedFile, atMostOnce()).writeText("hello")
        verify(mockedFile, times(3)).readText()
        verify(mockedFile, timeout(100)).readText()

        val mockOne = mock<Path>()
        val mockTwo = mock<Path>()
        val mockThree = mock<Path>()

        verifyNoInteractions(mockOne)
        verifyNoInteractions(mockTwo, mockThree)
        verifyNoMoreInteractions(mockOne, mockTwo)
    }

    @Test
    fun `Mockito - inOrder verification`() {
        // A. Single mock whose methods must be invoked in a particular order
        val singleMock = mock<MutableList<String>>()

        //using a single mock
        singleMock.add("was added first")
        singleMock.add("was added second")

        //create an inOrder verifier for a single mock
        val inOrder = inOrder(singleMock)

        //following will make sure that add is first called with "was added first",
        // then with "was added second"
        inOrder.verify(singleMock).add("was added first")
        inOrder.verify(singleMock).add("was added second")
    }

    /**
     * `argThat`
     *
     * The `argThat` argument matcher in Mockito lets you create advanced argument
     * matchers that run a function on passed arguments, and checks if the function
     * returns true.
     * If you have a complicated class that can’t be easily checked using .equals(),
     * a custom matcher can be a useful tool.
     */
    @Test
    fun `Mockito - argThat`() {
        val mockedCar = mock<Car>()

        whenever(
            mockedCar.drive(eq(1000), argThat { engine -> engine.dieselEngine })
        ).thenReturn(1500)

        assertThat(mockedCar.drive(1000, Engine(true))).isEqualTo(1500)
        assertThat(mockedCar.drive(1000, Engine(false))).isEqualTo(0)
    }

    /**
     * `ArgumentCaptor`
     *
     * When you need to run additional assertions on an argument, the `ArgumentCaptor`
     * is the tool for the job in Mockito. An `ArgumentCaptor` will keep track of
     * arguments passed to a mocked method, then allow you to retrieve the argument later.
     *
     * See: https://newbedev.com/mockito-argumentcaptor-for-kotlin-function
     * <-# Only @Captor and capture(personArgument) works! #->
     */

    @Captor
    lateinit var personArgument: ArgumentCaptor<Person>

    @Test // something's wrong ... here
    fun `Mockito - ArgumentCaptor`() {
        val mockPhone = mock<Phone>()
        // personArgument.capture() must not be null
        val personArgument = ArgumentCaptor.forClass(Person::class.java)

        mockPhone.call(Person("Sarah Jane", 33))

//        verify(mockPhone).call(personArgument.capture()) // personArgument.capture() must not be null
        verify(mockPhone).call(capture(personArgument))
        assertThat("Sarah Jane").isEqualTo(personArgument.value.name)
    }


    /**
     * Spy
     */

    @Test
    fun `Spy demo1`() {
        // Arrange (Given)
        val list: MutableList<String> = mutableListOf()
        val listSpy = spy(list)

        // Act (When)
        listSpy.add("first-element")
        println(listSpy.size)

        // Assert (Then)
        assertThat(listSpy[0]).isEqualTo("first-element")
    }

    @Test
    fun `Spy demo2`() {
        // Arrange (Given)
        val list: MutableList<String> = ArrayList()
        val listSpy = spy(list)

        // Act (When)
        listSpy.add("first-element")

        // Assert (Then)
        assertThat(listSpy[0]).isEqualTo("first-element")

        // Act (When) -- be careful!
        whenever(listSpy[0]).thenReturn("second-element")

        // Assert (Then)
        assertThat(listSpy[0]).isEqualTo("second-element")
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `Spy demo3`() {
        // Arrange (Given)
        val list: List<String> = ArrayList()
        val listSpy = spy(list)

        // Act (When) -- be careful!
        whenever(listSpy[0]).thenReturn("second-element")

        // Assert (Then)
        assertThat(listSpy[0]).isEqualTo("second-element")
    }

}