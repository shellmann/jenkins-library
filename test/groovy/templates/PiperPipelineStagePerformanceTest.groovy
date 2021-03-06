package templates

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import util.*

import static org.hamcrest.Matchers.anyOf
import static org.hamcrest.Matchers.containsString
import static org.hamcrest.Matchers.hasItems
import static org.hamcrest.Matchers.not
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertThat

class PiperPipelineStagePerformanceTest extends BasePiperTest {
    private JenkinsStepRule jsr = new JenkinsStepRule(this)
    private JenkinsLoggingRule jlr = new JenkinsLoggingRule(this)

    @Rule
    public RuleChain rules = Rules
        .getCommonRules(this)
        .around(new JenkinsReadYamlRule(this))
        .around(jlr)
        .around(jsr)

    private List stepsCalled = []
    private Map stepParameters = [:]

    @Before
    void init()  {
        binding.variables.env.STAGE_NAME = 'Performance'
        helper.registerAllowedMethod('piperStageWrapper', [Map.class, Closure.class], {m, body ->
            return body()
        })
        helper.registerAllowedMethod('gatlingExecuteTests', [Map.class], {m ->
            stepsCalled.add('gatlingExecuteTests')
            stepParameters.gatlingExecuteTests = m
        })
    }

    @Test
    void testStageDefault() {
        jsr.step.piperPipelineStagePerformance(
            script: nullScript,
            juStabUtils: utils,
        )
        assertThat(stepsCalled, not(anyOf(hasItems('gatlingExecuteTests'))))
    }

    @Test
    void testgatlingExecuteTests() {
        jsr.step.piperPipelineStagePerformance(
            script: nullScript,
            juStabUtils: utils,
            gatlingExecuteTests: true
        )
        assertThat(stepsCalled, hasItems('gatlingExecuteTests'))
        assertNotNull(stepParameters.gatlingExecuteTests)
    }
}
