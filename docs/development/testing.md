---
layout: default
title: Running with Java 
markdown: kramdown
---

# ValiPop Testing

Unit tests are structured as follows:

```
uk.ac.standrews.cs.valipop
│
├───config
│
├───conversion
│   ├───population
│   ├───records
│
├───internal
├───population
├───validation
```

<dl>

<dt>
<code>[config][1]</code>
</dt>

<dd>
Tests for correct processing of simulation configurations.
</dd>

<dt>
<code>[conversion][2]</code>
</dt>

<dd>
Tests for correct export of a generated population to GEDCOM file and to synthesised birth/death/marriage records, and
for correct import of a population from GEDCOM file.
</dd>

<dt>
<code>[internal][3]</code>
</dt>

<dd>
Tests for various aspects of internal simulation logic.
</dd>

<dt>
<code>[population][4]</code>
</dt>

<dd>
Tests for expected structural properties of a generated population.
</dd>

<dt>
<code>[validation][5]</code>
</dt>

<dd>
Tests for statistical validation of a generated population.
</dd>

</dl>

[1]: https://github.com/stacs-srg/valipop/tree/main/src/test/java/uk/ac/standrews/cs/valipop/config
[2]: https://github.com/stacs-srg/valipop/tree/main/src/test/java/uk/ac/standrews/cs/valipop/conversion
[3]: https://github.com/stacs-srg/valipop/tree/main/src/test/java/uk/ac/standrews/cs/valipop/internal
[4]: https://github.com/stacs-srg/valipop/tree/main/src/test/java/uk/ac/standrews/cs/valipop/population
[5]: https://github.com/stacs-srg/valipop/tree/main/src/test/java/uk/ac/standrews/cs/valipop/validation
