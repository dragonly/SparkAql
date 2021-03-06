package SparkAql.aql.execution

import SparkAql.aql.plan.TreeNode
import SparkAql.aql.plan.logical.LogicalPlan

/**
 * Given a Logical Plan, returns a list of `PhysicalPlan`s that can
 * be used for execution. If this strategy does not apply to the give logical operation then an
 * empty list should be returned.
 */
abstract class SparkStrategy[PhysicalPlan <: TreeNode[PhysicalPlan]] {
  def apply(plan: LogicalPlan): Seq[PhysicalPlan]
}

/**
 * Abstract class for transforming logical plans into physical plans.
 * Child classes are responsible for specifying a list of SparkStrategy objects that each of which
 * can return a list of possible physical plan options.  If a given strategy is unable to plan all
 * of the remaining operators in the tree, it can call [[planLater]], which returns a placeholder
 * object that will be filled in using other available strategies.
 *
 * TODO: RIGHT NOW ONLY ONE PLAN IS RETURNED EVER...
 *       PLAN SPACE EXPLORATION WILL BE IMPLEMENTED LATER.
 *
 * @tparam PhysicalPlan The type of physical plan produced by this [[QueryPlanner]]
 */
abstract class QueryPlanner[PhysicalPlan <: TreeNode[PhysicalPlan]] {
  /** A list of execution strategies that can be used by the planner */
  def strategies: Seq[SparkStrategy[PhysicalPlan]]

  /**
   * Returns a placeholder for a physical plan that executes `plan`. This placeholder will be
   * filled in automatically by the QueryPlanner using the other execution strategies that are
   * available.
   */
  protected def planLater(plan: LogicalPlan): PhysicalPlan = this.plan(plan).next()

  def plan(plan: LogicalPlan): Iterator[PhysicalPlan] = {
    // Obviously a lot to do here still...
    val iter = strategies.view.flatMap(_(plan)).toIterator
    assert(iter.hasNext, s"No plan for $plan")
    iter
  }
}
