import context.Context;
import context.ElementNotFoundException;
import expressions.*;
import expressions.binary.AddExpression;
import expressions.binary.DivExpression;
import expressions.binary.MulExpression;
import expressions.binary.SubExpression;
import expressions.exceptions.ContextIncompleteException;
import expressions.exceptions.DivByZeroException;
import org.junit.Test;
import values.DoubleValue;
import values.IntValue;
import values.MatrixValue;
import values.Value;
import org.junit.Assert;
import expressions.binary.*;

import java.util.LinkedList;
import java.util.List;

public class ExpressionTest {

    @Test
    public void test1() throws DivByZeroException, ContextIncompleteException {
        IntValue intValue = new IntValue(42);
        IntValue intValue1 = new IntValue(24);
        IntValue intValue2 = new IntValue(66);

        Expression<IntValue> intValueExpression = new ConstExpression<>(intValue);
        Expression<IntValue> intValueExpression1 = new ConstExpression<>(intValue1);
        Expression<IntValue> intValueExpression2 = new ConstExpression<>(intValue2);

        Expression<IntValue> intValueExpression3 = new AddExpression<>(intValueExpression, intValueExpression1);
        Expression<IntValue> intValueExpression4 = new AddExpression<>(intValueExpression3, intValueExpression3);

        Context<IntValue> intValueContext = new Context<>();
        IntValue result = intValueExpression4.evaluate(intValueContext);

        Assert.assertEquals(result.getValue(), 132);

        Value<IntValue> intValueValue = new IntValue(2);
        Value<IntValue> intValueValue1 = new IntValue(3);
    }

    @Test
    public void test2() throws DivByZeroException, ContextIncompleteException {
        Value<IntValue> intValueValue = new IntValue(2);
        IntValue intValue = new IntValue(5);
        IntValue intValue1 = new IntValue(3);
        IntValue intValue2 = new IntValue(1);

        AbstractExpression<IntValue> intValueAbstractExpression = new ConstExpression<>(intValue);
        AbstractExpression<IntValue> intValueAbstractExpression1 = new ConstExpression<>(intValue1);
        AbstractExpression<IntValue> intValueAbstractExpression2 = new ConstExpression<>(intValue2);



        AbstractExpression<IntValue> abstractExpression = new AddExpression<>(intValueAbstractExpression,intValueAbstractExpression1);

        AbstractExpression<IntValue> abstractExpression1 = new SubExpression<>(abstractExpression,intValueAbstractExpression2);
        Context<IntValue> intValueContext = new Context<>();
        Assert.assertEquals(7, abstractExpression1.evaluate(intValueContext).getValue());
    }

    @Test
    public void testIntValueContext() throws DivByZeroException, ContextIncompleteException {
        IntValue intValue = new IntValue(42);
        IntValue intValue1 = new IntValue(24);
        IntValue intValue2 = new IntValue(66);

        Expression<IntValue> intValueExpression = new ConstExpression<>(intValue);
        Expression<IntValue> intValueExpression1 = new VarExpression<>("foo");
        Expression<IntValue> intValueExpression2 = new VarExpression<>("bar");

        Expression<IntValue> intValueExpression3 = new AddExpression<>(intValueExpression1, intValueExpression);
        Expression<IntValue> intValueExpression4 = new MulExpression<>(intValueExpression3, intValueExpression2);

        Context<IntValue> intValueContext = new Context<>();
        intValueContext.setValue("foo", intValue1);
        intValueContext.setValue("bar", intValue2);

        IntValue result = intValueExpression4.evaluate(intValueContext);

        Assert.assertEquals(4356, result.getValue());
    }

    @Test
    public void testContextDouble() throws DivByZeroException, ContextIncompleteException, ElementNotFoundException {

        DoubleValue doubleValue = new DoubleValue(20.0);
        DoubleValue doubleValue1 = new DoubleValue(30.0);
        DoubleValue doubleValue2 = new DoubleValue(10.0);
        DoubleValue doubleValue3 = new DoubleValue(5.0);

        Context<DoubleValue> doubleValueContext = new Context<>();

        DoubleValue doubleValue4 = new DoubleValue(2.0);
        DoubleValue doubleValue5 = new DoubleValue(2.0);
        Expression<DoubleValue> doubleValueExpression4 = new ConstExpression<>(doubleValue4);
        Expression<DoubleValue> doubleValueExpression5 = new ConstExpression<>(doubleValue5);
        Expression<DoubleValue> dividableValue = new DivExpression<>(doubleValueExpression4, doubleValueExpression5);

        DoubleValue result1 = dividableValue.evaluate(doubleValueContext);

        Expression<DoubleValue> doubleValueExpression = new ConstExpression<>(doubleValue);
        Expression<DoubleValue> doubleValueExpression1 = new ConstExpression<>(doubleValue1);
        Expression<DoubleValue> doubleValueExpression2 = new VarExpression<>("foo");
        Expression<DoubleValue> doubleValueExpression3 = new VarExpression<>("bar");
        Expression<DoubleValue> doubleValueSubExpression = new SubExpression<>(doubleValueExpression,doubleValueExpression1);
        Expression<DoubleValue> doubleValueDivExpression = new DivExpression<>(doubleValueExpression2,doubleValueExpression3);
        Expression<DoubleValue> doubleValueAddExpression = new AddExpression<>(doubleValueSubExpression, doubleValueDivExpression);


        doubleValueContext.setValue("foo", doubleValue2);
        doubleValueContext.setValue("bar", doubleValue3);

        DoubleValue result = doubleValueAddExpression.evaluate(doubleValueContext);

        Assert.assertEquals(-8.0, result.getValue(), 0.1);
        Assert.assertEquals(false, doubleValueAddExpression.isConst());
    }

    @Test
    public void testCycles() throws DivByZeroException, ContextIncompleteException {

        IntValue intValue = new IntValue(1);
        IntValue intValue1 = new IntValue(2);
        IntValue intValue2 = new IntValue(2);
        IntValue intValue3 = new IntValue(2);

        Expression<IntValue> intValueExpression = new ConstExpression<>(intValue);
        Expression<IntValue> intValueExpression1 = new ConstExpression<>(intValue1);

        BinaryExpression<IntValue> binaryExpression = new AddExpression<>(intValueExpression, intValueExpression1);

        binaryExpression.setLeftExpression(binaryExpression);

        Context<IntValue> context = new Context<>();

        Assert.assertEquals(true, binaryExpression.hasCycles());

        Expression<IntValue> intValueExpression2 = new ConstExpression<>(intValue2);
        Expression<IntValue> intValueExpression3 = new ConstExpression<>(intValue3);

        BinaryExpression<IntValue> binaryExpression1 = new AddExpression<>(intValueExpression2, intValueExpression3);

        binaryExpression.setLeftExpression(binaryExpression1);

        Assert.assertEquals(false, binaryExpression.hasCycles());

        binaryExpression.setRightExpression(binaryExpression1);

        Assert.assertEquals(false, binaryExpression.hasCycles());


        Expression<IntValue> intValueExpression7 = new ConstExpression<>(intValue);
        Expression<IntValue> intValueExpression8 = new ConstExpression<>(intValue1);
        Expression<IntValue> intValueExpression9 = new ConstExpression<>(intValue);
        Expression<IntValue> intValueExpression10 = new ConstExpression<>(intValue1);

        BinaryExpression<IntValue> binaryExpression2 = new AddExpression<>(intValueExpression7, intValueExpression8);
        BinaryExpression<IntValue> binaryExpression3 = new AddExpression<>(intValueExpression7, intValueExpression8);

        binaryExpression2.setRightExpression(binaryExpression3);

        BinaryExpression<IntValue> binaryExpression4 = new AddExpression<>(binaryExpression2, binaryExpression3);

        Assert.assertEquals(false, binaryExpression4.hasCycles());

        binaryExpression3.setLeftExpression(binaryExpression2);

        Assert.assertEquals(true, binaryExpression4.hasCycles());

        ExpressionWrapper<IntValue> intValueExpressionWrapper = new ExpressionWrapper<>(binaryExpression3);

        Assert.assertEquals(true, intValueExpressionWrapper.hasCycles());
    }


    @Test
    public void testMatrixValue() {

        MatrixValue matrixValue = new MatrixValue(2, 2);
        MatrixValue matrixValue1 = new MatrixValue(2, 2);

        matrixValue.setValue(5, 0, 0);
        matrixValue.setValue(8, 0, 1);
        matrixValue.setValue(1, 1, 0);
        matrixValue.setValue(2, 1, 1);

        matrixValue1.setValue(7, 0, 0);
        matrixValue1.setValue(9, 0, 1);
        matrixValue1.setValue(3, 1, 0);
        matrixValue1.setValue(5, 1, 1);

        MatrixValue resultAddMatrix = matrixValue.add(matrixValue1);

        Assert.assertEquals(12, resultAddMatrix.getValue(0, 0), 0.1);
        Assert.assertEquals(17, resultAddMatrix.getValue(0, 1), 0.1);
        Assert.assertEquals(4, resultAddMatrix.getValue(1, 0), 0.1);
        Assert.assertEquals(7, resultAddMatrix.getValue(1, 1), 0.1);

        MatrixValue resultSubMatrix = matrixValue.sub(matrixValue1);

        Assert.assertEquals(-2, resultSubMatrix.getValue(0, 0), 0.1);
        Assert.assertEquals(-1, resultSubMatrix.getValue(0, 1), 0.1);
        Assert.assertEquals(-2, resultSubMatrix.getValue(1, 0), 0.1);
        Assert.assertEquals(-3, resultSubMatrix.getValue(1, 1), 0.1);

        MatrixValue resultMulMatrix = matrixValue.mul(matrixValue1);

        Assert.assertEquals(59, resultMulMatrix.getValue(0, 0), 0.1);
        Assert.assertEquals(85, resultMulMatrix.getValue(0, 1), 0.1);
        Assert.assertEquals(13, resultMulMatrix.getValue(1, 0), 0.1);
        Assert.assertEquals(19, resultMulMatrix.getValue(1, 1), 0.1);

        MatrixValue matrixValue2 = new MatrixValue(2, 3);
        MatrixValue matrixValue3 = new MatrixValue(3, 2);

        matrixValue2.setValue(5, 0, 0);
        matrixValue2.setValue(1, 0, 1);
        matrixValue2.setValue(5, 0, 2);
        matrixValue2.setValue(3, 1, 0);
        matrixValue2.setValue(74, 1, 1);
        matrixValue2.setValue(1, 1, 2);

        matrixValue3.setValue(7, 0, 0);
        matrixValue3.setValue(94, 0, 1);
        matrixValue3.setValue(34, 1, 0);
        matrixValue3.setValue(51, 1, 1);
        matrixValue3.setValue(7, 2, 0);
        matrixValue3.setValue(6, 2, 1);

        MatrixValue resultMulMatrix1 = matrixValue2.mul(matrixValue3);
        Assert.assertEquals(2, resultMulMatrix1.getCols());
        Assert.assertEquals(2, resultMulMatrix1.getRows());

        Assert.assertEquals(104, resultMulMatrix1.getValue(0, 0), 0.1);
        Assert.assertEquals(551, resultMulMatrix1.getValue(0, 1), 0.1);
        Assert.assertEquals(2544, resultMulMatrix1.getValue(1, 0), 0.1);
        Assert.assertEquals(4062, resultMulMatrix1.getValue(1, 1), 0.1);


        MatrixValue matrixValue4 = new MatrixValue(4, 4);
        MatrixValue matrixValue5 = new MatrixValue(5, 4);

    }

    @Test
    public void testValueAppentToList() {

        List<Value<IntValue>> valueList = new LinkedList<>();

        IntValue intValue = new IntValue(23);

        intValue.appendToList(valueList);
    }


    @Test
    public void testIsConstant() {
        IntValue intValue = new IntValue(1);
        IntValue intValue1 = new IntValue(2);

        Expression<IntValue> intValueExpression = new ConstExpression<>(intValue);
        Expression<IntValue> intValueExpression1 = new VarExpression<>("intValue1");

        BinaryExpression<IntValue> binaryExpression = new AddExpression<>(intValueExpression, intValueExpression1);

        Assert.assertEquals(false, binaryExpression.isConst());
    }
    @Test
    public void testExpressonWrapper() throws DivByZeroException, ContextIncompleteException {

        IntValue intValue = new IntValue(2);
        IntValue intValue1 = new IntValue(3);

        ConstExpression<IntValue> constExpression = new ConstExpression<>(intValue);
        ExpressionWrapper<IntValue> expressionWrapper = new ExpressionWrapper<>(constExpression);

        Assert.assertEquals(true, expressionWrapper.isConst());
        Assert.assertEquals(false, !expressionWrapper.isConst());

        VarExpression<IntValue> varExpression = new VarExpression<>("foo");
        ExpressionWrapper<IntValue> expressionWrapper1 = new ExpressionWrapper<>(varExpression);

        Assert.assertEquals(false, expressionWrapper1.isConst());

        Expression<IntValue> expression = new AddExpression<>(expressionWrapper, expressionWrapper1);
        ExpressionWrapper<IntValue> expressionWrapper3 = new ExpressionWrapper<>(expression);

        Context<IntValue> intValueContext = new Context<>();
        intValueContext.setValue("foo", intValue1);
        IntValue result = expressionWrapper3.evaluate(intValueContext);

        Assert.assertEquals(5, result.getValue());


    }

    @Test(expected = DivByZeroException.class)
    public void testDividedByZero() throws DivByZeroException {

        IntValue intValue = new IntValue(2);
        IntValue intValue1 = new IntValue(0);

        IntValue result = intValue1.div(intValue);

        DoubleValue doubleValue = new DoubleValue(2);
        DoubleValue doubleValue1 = new DoubleValue(0);

        DoubleValue result1 =doubleValue.div(doubleValue1);
    }

    @Test(expected = ElementNotFoundException.class)
    public void testElementNotFoundExpeption() throws ElementNotFoundException {

        Context<IntValue> intValueContext = new Context<>();

        IntValue intValue = new IntValue(2);
        IntValue intValue1 = new IntValue(3);

        intValueContext.setValue("foo", intValue);
        intValueContext.setValue("bar", intValue1);

        Assert.assertEquals(true, intValueContext.has("foo"));
        Assert.assertEquals(true, intValueContext.has("bar"));

        intValueContext.getValue("poo");
    }

    @Test(expected = ContextIncompleteException.class)
    public void testContextIncompleteException() throws DivByZeroException, ContextIncompleteException {

        Context<IntValue> intValueContext = new Context<>();

        IntValue intValue = new IntValue(2);
        IntValue intValue1 = new IntValue(3);
        IntValue intValue2 = new IntValue(3);

        intValueContext.setValue("foo", intValue);
        intValueContext.setValue("bar", intValue1);

        VarExpression<IntValue> valueVarExpression = new VarExpression<>("foo");
        VarExpression<IntValue> valueVarExpression1 = new VarExpression<>("poo");

        BinaryExpression<IntValue> binaryExpression = new AddExpression<>(valueVarExpression, valueVarExpression1);

        binaryExpression.evaluate(intValueContext);
    }


    @Test
    public void testToString()  {

        IntValue intValue = new IntValue(1);
        IntValue intValue1 = new IntValue(2);
        IntValue intValue2 = new IntValue(2);
        IntValue intValue3 = new IntValue(2);

        Expression<IntValue> intValueExpression = new ConstExpression<>(intValue);
        Expression<IntValue> intValueExpression1 = new ConstExpression<>(intValue1);

        BinaryExpression<IntValue> binaryExpression = new AddExpression<>(intValueExpression, intValueExpression1);

        binaryExpression.toString();

    }


}
