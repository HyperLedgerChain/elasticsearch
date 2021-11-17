/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.search.runtime;

import org.apache.lucene.search.Query;
import org.elasticsearch.script.Script;
import org.elasticsearch.test.ESTestCase;

import static org.hamcrest.Matchers.equalTo;

public class LongScriptFieldTermQueryTests extends AbstractLongScriptFieldQueryTestCase<LongScriptFieldTermQuery> {
    @Override
    protected LongScriptFieldTermQuery createTestInstance() {
        return new LongScriptFieldTermQuery(randomScript(), randomAlphaOfLength(5), randomApproximation(), leafFactory, randomLong());
    }

    @Override
    protected LongScriptFieldTermQuery copy(LongScriptFieldTermQuery orig) {
        return new LongScriptFieldTermQuery(orig.script(), orig.fieldName(), orig.approximation(), leafFactory, orig.term());
    }

    @Override
    protected LongScriptFieldTermQuery mutate(LongScriptFieldTermQuery orig) {
        Script script = orig.script();
        String fieldName = orig.fieldName();
        Query approximation = orig.approximation();
        long term = orig.term();
        switch (randomInt(3)) {
            case 0:
                script = randomValueOtherThan(script, this::randomScript);
                break;
            case 1:
                fieldName += "modified";
                break;
            case 2:
                approximation = randomValueOtherThan(approximation, this::randomApproximation);
                break;
            case 3:
                term = randomValueOtherThan(term, ESTestCase::randomLong);
                break;
            default:
                fail();
        }
        return new LongScriptFieldTermQuery(script, fieldName, approximation, leafFactory, term);
    }

    @Override
    public void testMatches() {
        LongScriptFieldTermQuery query = new LongScriptFieldTermQuery(randomScript(), "test", randomApproximation(), leafFactory, 1);
        assertTrue(query.matches(new long[] { 1 }, 1));     // Match because value matches
        assertFalse(query.matches(new long[] { 2 }, 1));    // No match because wrong value
        assertFalse(query.matches(new long[] { 2, 1 }, 1)); // No match because value after count of values
        assertTrue(query.matches(new long[] { 2, 1 }, 2));  // Match because one value matches
    }

    @Override
    protected void assertToString(LongScriptFieldTermQuery query) {
        assertThat(query.toString(query.fieldName()), equalTo(query.term() + " approximated by " + query.approximation()));
    }
}