package interpreter.model.type;

import interpreter.model.value.ToyReferenceValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ToyReferenceTypeTest {

    private static ToyReferenceType ref(IType inner) {
        return new ToyReferenceType(inner);
    }

    @Test
    void defaultValueIsNullReferenceToInnerType() {
        assertEquals(new ToyReferenceValue(0, new ToyIntegerType()), ref(new ToyIntegerType()).defaultValue());
    }

    @Test
    void defaultValueOfNestedReferenceKeepsTheInnerReferenceType() {
        assertEquals(new ToyReferenceValue(0, ref(new ToyIntegerType())),
                ref(ref(new ToyIntegerType())).defaultValue());
    }

    @Test
    void defaultValueHasTheReferenceTypeItself() {
        ToyReferenceType type = ref(ref(new ToyBooleanType()));
        assertEquals(type, type.defaultValue().getType());
    }

    @Test
    void getInnerReturnsTheWrappedType() {
        assertEquals(new ToyStringType(), ref(new ToyStringType()).getInner());
    }

    @Test
    void referencesToTheSameTypeAreEqual() {
        assertEquals(ref(new ToyIntegerType()), ref(new ToyIntegerType()));
    }

    @Test
    void nestedReferencesAreComparedRecursively() {
        assertEquals(ref(ref(new ToyIntegerType())), ref(ref(new ToyIntegerType())));
    }

    @Test
    void equalNestedReferencesHaveSameHashCode() {
        assertEquals(ref(ref(new ToyIntegerType())).hashCode(), ref(ref(new ToyIntegerType())).hashCode());
    }

    @Test
    void referencesToDifferentTypesAreNotEqual() {
        assertNotEquals(ref(new ToyIntegerType()), ref(new ToyBooleanType()));
    }

    @Test
    void differentNestingDepthsAreNotEqual() {
        assertNotEquals(ref(ref(new ToyIntegerType())), ref(new ToyIntegerType()));
        assertNotEquals(ref(new ToyIntegerType()), ref(ref(new ToyIntegerType())));
    }

    @Test
    void referenceIsNotEqualToItsInnerType() {
        assertNotEquals(ref(new ToyIntegerType()), new ToyIntegerType());
        assertNotEquals(new ToyIntegerType(), ref(new ToyIntegerType()));
    }

    @Test
    void referenceTypeIsNotEqualToNull() {
        assertNotEquals(null, ref(new ToyIntegerType()));
    }

    @Test
    void toStringWrapsInnerType() {
        assertEquals("Ref(int)", ref(new ToyIntegerType()).toString());
    }

    @Test
    void toStringOfNestedReference() {
        assertEquals("Ref(Ref(int))", ref(ref(new ToyIntegerType())).toString());
    }
}
