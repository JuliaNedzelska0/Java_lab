package ua.edu.tickets;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public final class NameOrder {
    public static final Comparator<String> UKRAINIAN =
            Collator.getInstance(Locale.of("uk"))::compare;

    private NameOrder() {}
}
