package interpreter.model.value;

import interpreter.model.type.ToyBooleanType;
import interpreter.model.type.ToyIntegerType;
import interpreter.model.type.ToyReferenceType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ToyReferenceValueTest {

    @Test
    void gettersReturnAddressAndLocationType() {
        ToyReferenceValue value = new ToyReferenceValue(3, new ToyIntegerType());
        assertEquals(3, value.getAddress());
        assertEquals(new ToyIntegerType(), value.getLocationType());
    }

    @Test
    void typeIsReferenceToLocationType() {
        assertEquals(new ToyReferenceType(new ToyIntegerType()),
                new ToyReferenceValue(1, new ToyIntegerType()).getType());
    }

    @Test
    void typeOfNestedReference() {
        assertEquals(new ToyReferenceType(new ToyReferenceType(new ToyIntegerType())),
                new ToyReferenceValue(2, new ToyReferenceType(new ToyIntegerType())).getType());
    }

    @Test
    void nullReferenceStillHasAType() {
        assertEquals(new ToyReferenceType(new ToyBooleanType()),
                new ToyReferenceValue(0, new ToyBooleanType()).getType());
    }

    @Test
    void valuesWithSameAddressAndTypeAreEqual() {
        assertEquals(new ToyReferenceValue(1, new ToyIntegerType()), new ToyReferenceValue(1, new ToyIntegerType()));
    }

    @Test
    void equalValuesHaveSameHashCode() {
        assertEquals(new ToyReferenceValue(1, new ToyIntegerType()).hashCode(),
                new ToyReferenceValue(1, new ToyIntegerType()).hashCode());
    }

    @Test
    void differentAddressesAreNotEqual() {
        assertNotEquals(new ToyReferenceValue(1, new ToyIntegerType()), new ToyReferenceValue(2, new ToyIntegerType()));
    }

    @Test
    void differentLocationTypesAreNotEqual() {
        assertNotEquals(new ToyReferenceValue(1, new ToyIntegerType()), new ToyReferenceValue(1, new ToyBooleanType()));
    }

    @Test
    void referenceValueIsNotEqualToAnIntegerWithTheSameNumber() {
        assertNotEquals(new ToyReferenceValue(1, new ToyIntegerType()), new ToyIntegerValue(1));
    }

    @Test
    void referenceValueIsNotEqualToNull() {
        assertNotEquals(null, new ToyReferenceValue(1, new ToyIntegerType()));
    }

    @Test
    void toStringIsAddressAndLocationType() {
        assertEquals("(1, int)", new ToyReferenceValue(1, new ToyIntegerType()).toString());
    }

    @Test
    void toStringOfNestedReference() {
        assertEquals("(2, Ref(int))", new ToyReferenceValue(2, new ToyReferenceType(new ToyIntegerType())).toString());
    }
}
