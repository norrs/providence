package net.morimekta.providence.testing;

import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageVariant;
import net.morimekta.providence.PUnion;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.util.PTypeUtils;
import net.morimekta.util.Binary;
import net.morimekta.util.Strings;
import net.morimekta.util.json.JsonException;
import net.morimekta.util.json.JsonWriter;

import junit.framework.AssertionFailedError;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Stein Eldar Johnsen
 * @since 21.01.16.
 */
public class MessageEq<T extends PMessage<T>> extends BaseMatcher<T> {
    private final PMessage<T> expected;

    public MessageEq(PMessage<T> expected) {
        this.expected = expected;
    }

    @Override
    public boolean matches(Object actual) {
        if (expected == null) {
            return actual == null;
        }
        if (!(actual instanceof PMessage)) {
            throw new AssertionFailedError("Item " + actual.getClass().toString() + " not a providence message.");
        }
        return expected.equals(actual);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("equals(")
                   .appendText(limitToString(expected))
                   .appendText(")");
    }

    @Override
    public void describeMismatch(Object actual, Description mismatchDescription) {
        if (expected == null) {
            mismatchDescription.appendText("got " + toString(actual));
        } else if (actual == null) {
            mismatchDescription.appendText("got null");
        } else {
            LinkedList<String> mismatches = new LinkedList<>();
            collectMismatches("", (PMessage) expected, (PMessage) actual, mismatches);
            if (mismatches.size() == 1) {
                mismatchDescription.appendText(mismatches.getFirst());
            } else {
                boolean first = true;
                mismatchDescription.appendText("[");
                int i = 0;
                for (String mismatch : mismatches) {
                    if (first) {
                        first = false;
                    } else {
                        mismatchDescription.appendText(",");
                    }
                    mismatchDescription.appendText("\n        ");
                    if (i >= 20) {
                        int remaining = mismatches.size() - i;
                        mismatchDescription.appendText("... and " + remaining + "more");
                        break;
                    }
                    mismatchDescription.appendText(mismatch);
                    ++i;
                }
                mismatchDescription.appendText("\n     ]");
            }
        }
    }

    protected static <T extends PMessage<T>> void collectMismatches(String xPath,
                                                                    T expected,
                                                                    T actual,
                                                                    LinkedList<String> mismatches) {
        // This is pretty heavy calculation, but since it's only done on
        // mismatch / test failure, it should be fine.
        if (expected.descriptor()
                    .getVariant() == PMessageVariant.UNION) {
            PUnion<?> eu = (PUnion) expected;
            PUnion<?> ac = (PUnion) actual;

            if (!eu.unionField()
                   .equals(ac.unionField())) {
                mismatches.add(String.format("%s to have %s, but had %s",
                                             xPath,
                                             eu.unionField()
                                               .getName(),
                                             ac.unionField()
                                               .getName()));
            }
        }

        for (PField<?> field : expected.descriptor()
                                       .getFields()) {
            int key = field.getKey();
            String fieldXPath = xPath.isEmpty() ? field.getName() : xPath + "." + field.getName();

            if (expected.has(key) != actual.has(key)) {
                if (!expected.has(key)) {
                    mismatches.add(String.format("%s to be missing, but was %s",
                                                 fieldXPath,
                                                 toString(actual.get(field.getKey()))));
                } else if (!actual.has(key)) {
                    mismatches.add(String.format("%s to be %s, but was missing",
                                                 fieldXPath,
                                                 toString(expected.get(field.getKey()))));
                }
            } else if (!PTypeUtils.equals(expected.get(key), actual.get(key))) {
                switch (field.getType()) {
                    case MESSAGE: {
                        collectMismatches(fieldXPath,
                                          (PMessage) expected.get(key),
                                          (PMessage) actual.get(key),
                                          mismatches);
                        break;
                    }
                    case LIST: {
                        collectListMismatches(fieldXPath, (List) expected.get(key), (List) actual.get(key), mismatches);
                        break;
                    }
                    case SET: {
                        collectSetMismatches(fieldXPath, (Set) expected.get(key), (Set) actual.get(key), mismatches);
                        break;
                    }
                    case MAP: {
                        collectMapMismatches(fieldXPath, (Map) expected.get(key), (Map) actual.get(key), mismatches);
                        break;
                    }
                    default: {
                        mismatches.add(String.format("%s was %s, expected %s",
                                                     fieldXPath,
                                                     toString(actual.get(field.getKey())),
                                                     toString(expected.get(field.getKey()))));
                        break;
                    }
                }
            }
        }
    }

    protected static <K, V> void collectMapMismatches(String xPath,
                                                      Map<K, V> expected,
                                                      Map<K, V> actual,
                                                      LinkedList<String> mismatches) {
        mismatches.addAll(actual.keySet()
                                .stream()
                                .filter(key -> !expected.keySet()
                                                        .contains(key))
                                .map(key -> String.format("found unexpected entry (%s, %s) in %s",
                                                          toString(key),
                                                          toString(actual.get(key)),
                                                          xPath))
                                .collect(Collectors.toList()));

        for (K key : expected.keySet()) {
            if (!actual.keySet()
                       .contains(key)) {
                mismatches.add(String.format("did not find entry (%s, %s) in in %s",
                                             toString(key),
                                             toString(expected.get(key)),
                                             xPath));
            } else {
                V exp = expected.get(key);
                V act = actual.get(key);
                if (!PTypeUtils.equals(exp, act)) {
                    // value differs.
                    String keyedXPath = String.format("%s[%s]", xPath, toString(key));
                    if (exp == null || act == null) {
                        mismatches.add(String.format("%s was %s, should be %s",
                                                     keyedXPath,
                                                     toString(exp),
                                                     toString(act)));
                    } else if (act instanceof PMessage) {
                        collectMismatches(keyedXPath, (PMessage) exp, (PMessage) act, mismatches);
                    } else {
                        mismatches.add(String.format("%s was %s, should be %s",
                                                     keyedXPath,
                                                     toString(act),
                                                     toString(exp)));
                    }
                }
            }
        }
    }

    protected static <T> void collectSetMismatches(String xPath,
                                                   Set<T> expected,
                                                   Set<T> actual,
                                                   LinkedList<String> mismatches) {
        // order does NOT matter regardless of type. The only
        // errors are missing and unexpected values. Partial
        // matches are not checked.
        mismatches.addAll(actual.stream()
                                .filter(item -> !expected.contains(item))
                                .map(item -> String.format("found unexpected set value %s in %s",
                                                           toString(item),
                                                           xPath))
                                .collect(Collectors.toList()));

        mismatches.addAll(expected.stream()
                                  .filter(item -> !actual.contains(item))
                                  .map(item -> String.format("did not find value %s in %s", toString(item), xPath))
                                  .collect(Collectors.toList()));

    }

    protected static <T> void collectListMismatches(String xPath,
                                                    List<T> expected,
                                                    List<T> actual,
                                                    LinkedList<String> mismatches) {
        Set<T> handledItems = new HashSet<>();

        boolean hasReorder = false;
        LinkedList<String> reordering = new LinkedList<>();
        for (int expectedIndex = 0; expectedIndex < expected.size(); ++expectedIndex) {
            String indexedXPath = String.format("%s[%d]", xPath, expectedIndex);
            T expectedItem = expected.get(expectedIndex);
            handledItems.add(expectedItem);

            T actualItem = actual.size() > expectedIndex ? actual.get(expectedIndex) : null;
            if (PTypeUtils.equals(expectedItem, actualItem)) {
                continue;
            }
            int actualIndex = actual.indexOf(expectedItem);

            int actualItemExpectedIndex = -1;
            if (actualItem != null) {
                actualItemExpectedIndex = expected.indexOf(actualItem);
            }

            if (actualIndex < 0) {
                reordering.add("NaN");
                // this item is missing.
                if (actualItemExpectedIndex < 0) {
                    handledItems.add(actualItem);
                    // replaced with new item, diff them normally.
                    if (actualItem instanceof PMessage) {
                        collectMismatches(indexedXPath, (PMessage) expectedItem, (PMessage) actualItem, mismatches);
                    } else {
                        mismatches.add(String.format("expected %s to be %s, but was %s",
                                                     indexedXPath,
                                                     toString(expectedItem),
                                                     toString(actualItem)));
                    }
                } else {
                    // the other item is reordered, so this is blindly inserted.
                    mismatches.add(String.format("missing item %s in %s", toString(expectedItem), indexedXPath));
                }
            } else if (actualIndex != expectedIndex) {
                reordering.add(String.format("%+d", actualIndex - expectedIndex));
                hasReorder = true;
            } else {
                reordering.add("±0");
            }
        }
        for (int actualIndex = 0; actualIndex < actual.size(); ++actualIndex) {
            T actualItem = actual.get(actualIndex);
            if (handledItems.contains(actualItem)) {
                continue;
            }
            if (expected.contains(actualItem)) {
                continue;
            }
            String indexedXPath = String.format("%s[%d]", xPath, actualIndex);
            mismatches.add(String.format("unexpected item %s in %s", toString(actualItem), indexedXPath));
        }
        if (hasReorder) {
            mismatches.add(String.format("unexpected item ordering in %s: [%s]", xPath, Strings.join(",", reordering)));
        }

    }

    protected static String toString(Object o) {
        if (o == null) {
            return "null";
        } else if (o instanceof PMessage) {
            return limitToString((PMessage) o);
        } else if (o instanceof PEnumValue) {
            return ((PEnumValue) o).descriptor()
                                   .getName() + "." + ((PEnumValue) o).getName();
        } else if (o instanceof Map) {
            return "{" + Strings.join(",",
                                      ((Map<?, ?>) o).entrySet()
                                                     .stream()
                                                     .map(e -> toString(e.getKey()) + ":" + toString(e.getValue()))
                                                     .collect(Collectors.toList())) + "}";
        } else if (o instanceof Collection) {
            return "[" + Strings.join(",",
                                      (Collection<String>) ((Collection<?>) o).stream()
                                                                              .map(MessageEq::toString)
                                                                              .collect(Collectors.toList())) + "]";
        } else if (o instanceof CharSequence) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JsonWriter writer = new JsonWriter(baos);
            try {
                writer.value((CharSequence) o);
                writer.flush();
                return new String(baos.toByteArray(), UTF_8);
            } catch (JsonException e) {
                return "\"" + o.toString() + "\"";
            }
        } else if (o instanceof Binary) {
            int len = ((Binary) o).length();
            if (len > 110) {
                return String.format("binary[%s...+%d]",
                                     ((Binary) o).toHexString()
                                                 .substring(0, 100),
                                     len - 50);
            } else {
                return "binary[" + ((Binary) o).toHexString() + "]";
            }
        } else if (o instanceof Double) {
            long l = ((Double) o).longValue();
            if (o.equals((double) l)) {
                return Long.toString(l);
            } else {
                return o.toString();
            }
        } else {
            return o.toString();
        }
    }

    protected static String limitToString(PMessage<?> message) {
        String tos = message == null ? "null" : message.asString();
        if (tos.length() > 120) {
            tos = tos.substring(0, 110) + "...}";
        }

        return tos;
    }
}
