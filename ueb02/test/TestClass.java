import org.junit.Assert;
import org.junit.Test;
import streamops.Person;
import streamops.PrintableChar;
import streamops.StreamOperations;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static streamops.PrintableChar.LOWER;
import static streamops.PrintableChar.RANGE;
import static streamops.StreamOperations.caesar;

public class TestClass {

    @Test
    public void testCollatz() {

        int[] actual = StreamOperations.collatz(1).limit(10).toArray();

        int[] expected = {1, 4, 2, 1, 4, 2, 1, 4, 2, 1};

        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void testCaesar() {


        PrintableChar c = new PrintableChar('F');

        PrintableChar c1 = c.encode(32);

        Assert.assertEquals('f', c1.toChar());

        PrintableChar c2 = new PrintableChar('f');

        PrintableChar c3 = c2.encode(63);

        Assert.assertEquals('F', c3.toChar());

        String encodeFOO = StreamOperations.caesar("FOO", 32, true);

        Assert.assertEquals("foo", encodeFOO);

        String encodefoo = StreamOperations.caesar("foo", 63, true);

        Assert.assertEquals("FOO", encodefoo);

        String decodeFOO = StreamOperations.caesar("foo", 32, false);

        Assert.assertEquals("FOO", decodeFOO);
    }

    @Test
    public void testVignere() {

        String encodeHalloWelt = StreamOperations.vigenere("Hallo Welt", "Foo", true);

        Assert.assertEquals("/q|S 0>u|[", encodeHalloWelt);

        PrintableChar printableChar = new PrintableChar('/');
        PrintableChar result = printableChar.decode('F');
        Assert.assertEquals('H', result.toChar());
        PrintableChar printableChar1 = new PrintableChar('q');
        PrintableChar result1 = printableChar1.decode('o');
        Assert.assertEquals('a', result1.toChar());
        PrintableChar printableChar2 = new PrintableChar('|');
        PrintableChar result2 = printableChar2.decode('o');
        Assert.assertEquals('l', result2.toChar());

        String decodeHalloWelt = StreamOperations.vigenere("/q|S 0>u|[", "Foo", false);

        Assert.assertEquals("Hallo Welt", decodeHalloWelt);

        PrintableChar printableChar3 = new PrintableChar('f');
        PrintableChar result3 = printableChar3.encode('~');
        Assert.assertEquals('&', result3.toChar());
    }

    @Test
    public void testOneTimePad() {

        Stream<PrintableChar> printableCharStream = "Hallo Welt".chars().mapToObj(c -> new PrintableChar(c));
        Stream<Integer> integerStream = Arrays.stream(new Integer[]{70, 111, 111, 70, 111, 111, 70, 111, 111, 70});

        StreamOperations.oneTimePad(printableCharStream, integerStream, true);

    }

    @Test
    public void testGroupWordsOfSameLength() {

        Stream<String> stringStream = Arrays.stream(new String[] {"ab", "cd", "def"});

        Map<Integer, Set<String>> integerStringMap = StreamOperations.groupWordsOfSameLength(stringStream);

        Set<String> stringSet = integerStringMap.get(2);
        Set<String> expected = new HashSet<>();
        expected.add("ab");
        expected.add("cd");

        Assert.assertEquals(2, stringSet.size());
        Assert.assertEquals(true, stringSet.containsAll(expected));

        Set<String> stringSet1 = integerStringMap.get(3);
        Set<String> expected1 = new HashSet<>();
        expected1.add("def");

        Assert.assertEquals(1, stringSet1.size());
        Assert.assertEquals(true, stringSet1.containsAll(expected1));


        Stream<String> stringStream1 = Arrays.stream(new String[] {});

        Map<Integer, Set<String>> integerStringMap1 = StreamOperations.groupWordsOfSameLength(stringStream1);

        Assert.assertEquals(true, integerStringMap1.isEmpty());
    }

    @Test
    public void countChars() {

        char[] chars = {'a' , 'a', 'b', 'c'};

        Stream<Character> characterStream = IntStream.range(0, chars.length).mapToObj(i -> chars[i]);

        Map<Character, Integer> characterIntegerMap = StreamOperations.countChars(characterStream, 'a', 'c', null);

        int a = characterIntegerMap.get('a');
        Assert.assertEquals(2, a);
        int b = characterIntegerMap.get('b');
        Assert.assertEquals(1, b);
        int c = characterIntegerMap.get('b');
        Assert.assertEquals(1, c);

        StreamOperations.oneTimePadPassphrase().limit(10l).forEach(System.out::println);

    }

    @Test
    public void stringToChars() {

        Character[] expected = {'a', 'b', 'c', 'd', 'a'};

        Character[] actual = StreamOperations.stringsToChars(Arrays.stream(new String[] {"ab", "cda"})).toArray(Character[]::new);

        Assert.assertArrayEquals(expected, actual);

        Character[] expected1 = {'a', 'b', 'c', 'd', 'a', ' ', 'd', 'e'};

        Character[] actual1 = StreamOperations.stringsToChars(Arrays.stream(new String[] {"ab", "cda", " ", "de"})).toArray(Character[]::new);

        Assert.assertArrayEquals(expected1, actual1);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testEvaluate() {

        String[] expression = {"5", "+", "6"};
        Stream<String> stringStream = Arrays.stream(expression);
        Map<String, String> stringStringMap = new HashMap<>();
        int integer = StreamOperations.evaluate(stringStream, stringStringMap);

        Assert.assertEquals(11, integer);
        String[] expression2 = {"6" , "/", "6", ""};
        Stream<String> stringStream2 = Arrays.stream(expression2);
        int integer2 = StreamOperations.evaluate(stringStream2, stringStringMap);

        System.out.println("das ergebnis " + integer2);
        stringStringMap.put("plus", "+");
        stringStringMap.put("durch", "/");
        stringStringMap.put("Eins", "1");
        stringStringMap.put("Drei", "3");


        Stream<String> stringStream1 = Arrays.stream(new String[]{"3", "*", "5", "+", "-5" ,"+", "2", "durch", "Drei", "plus", "Eins"});

        int integer3 = StreamOperations.evaluate(stringStream1, stringStringMap);;

        Assert.assertEquals(5, integer3);



    }

    @Test
    public void testGroupPersonsByGender() {



        Person person1 = new Person(Person.Gender.FEMALE, "pepa", "pepa", 22880, 2000);
        Person femaleUnderWage = new Person(Person.Gender.MALE, "pepe2", "pepe2", 22880, 999);
        Person femaleOverWage = new Person(Person.Gender.MALE, "pepe2", "pepe2", 22880, 20001);
        Person femaleExcludedZip = new Person(Person.Gender.MALE, "pepe2", "pepe2", 89332, 2000);

        Person person2 = new Person(Person.Gender.DIVERSE, "pepeit", "pepeit", 22880, 2000);
        Person itmaleUnderWage = new Person(Person.Gender.MALE, "pepe2", "pepe2", 22880, 999);
        Person itmaleOverWage = new Person(Person.Gender.MALE, "pepe2", "pepe2", 22880, 20001);
        Person itmaleExcludedZip = new Person(Person.Gender.MALE, "pepe2", "pepe2", 89332, 2000);

        Person person = new Person(Person.Gender.MALE, "pepe", "pepe", 22880, 2000);
        Person maleUnderWage = new Person(Person.Gender.MALE, "pepe2", "pepe2", 22880, 999);
        Person maleOverWage = new Person(Person.Gender.MALE, "pepe2", "pepe2", 22880, 20001);
        Person maleExcludedZip = new Person(Person.Gender.MALE, "pepe2", "pepe2", 89332, 2000);

        Set<Integer> zipCodes = new HashSet<>();
        zipCodes.add(22880);
        zipCodes.add(101010);
        zipCodes.add(598659);

        Stream<Person> personStream = Arrays.stream(new Person[] {person, person1, person2, person, person2, person1,
                maleExcludedZip, maleUnderWage, maleOverWage, femaleExcludedZip,femaleOverWage,femaleUnderWage,
                itmaleExcludedZip, itmaleOverWage, itmaleUnderWage});

         Map<Person.Gender, Set<Person>> genderSetMap = StreamOperations.groupPersonsByGender(personStream, zipCodes,1000, 2001);

         Assert.assertEquals(1, genderSetMap.get(Person.Gender.MALE).size());
         Assert.assertEquals(1, genderSetMap.get(Person.Gender.FEMALE).size());
         Assert.assertEquals(1, genderSetMap.get(Person.Gender.DIVERSE).size());

        //Person person2 = new Person(Person.Gender.MALE, "pepe", "pepe", 22880, 2000);


        Stream<Person> personStream1 = Arrays.stream(new Person[] {person});
         Map<Person.Gender, Set<Person>> genderSetMap1 = StreamOperations.groupPersonsByGender(personStream1, null,1000, 2001);

        Assert.assertEquals(1, genderSetMap1.get(Person.Gender.MALE).size());
    }


}
