/*
 * Copyright 2011 Andlabs, GbR.
 * based on the PriorityMap implementation of 2010 Stephen D. Strowes
 * http://svn.sdstrowes.co.uk/pub/util/PriorityMap.scala
 *
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OsmRouting is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.stylejy.app.PathPlanningSystem.Algorithms

import com.github.stylejy.app.PathPlanningSystem.NodeModel.GraphNode
/**
  * Created by stylejy on 17/06/2017.
  */
class AlgoBinaryHeap {
  class BackingArray[A] extends java.util.ArrayList[A] {
    override def get(index: Int): A = super.get(index - 1)
    override def set(index: Int, value: A): A = super.set(index - 1, value)
    override def remove(index: Int): A = super.remove(index - 1)
  }

  val heap = new BackingArray[GraphNode]()

  def isEmpty: Boolean = heap.isEmpty()

  def insert(node: GraphNode) = {
    heap.add(node) // relaxed
    //Index number to show the order.
    node.index = heap.size()
    bubbleUp(node.index)
  }

  def decreaseKey(node: GraphNode) {
    if (node.index > 1 && node.dist < heap.get(node.index/2).dist)
      bubbleUp(node.index)
    else
      bubbleDown(node.index)
  }

  def extractMin = {
    val min = heap.get(1)
    if (heap.size > 1) {
      val temp = heap.remove(heap.size())
      heap.set(1, temp)
      temp.index = 1
      bubbleDown(1)
    } else heap.remove(1)
    min.index = -1 // settled
    min
  }

  private def bubbleUp(index: Int): Unit = {
    if (index > 1 && heap.get(index).dist < heap.get(index/2).dist) {
      val tempA = heap.get(index)
      val tempB = heap.get(index/2)
      heap.set(index,   tempB)
      heap.set(index/2, tempA)
      tempA.index = index/2
      tempB.index = index
      bubbleUp(index/2)
    }
  }

  private def bubbleDown(index: Int): Unit = {
    val lChild = index*2
    if (lChild > heap.size()) return
    val rChild = lChild+1
    val lVal = heap.get(lChild).dist
    val s = if (rChild <= heap.size() && lVal < heap.get(rChild).dist) lChild
    else if (rChild <= heap.size() && lVal >= heap.get(rChild).dist) rChild
    else lChild
    if (heap.get(s).dist < heap.get(index).dist) {
      val tempA = heap.get(s)
      val tempB = heap.get(index)
      heap.set(s,     tempB)
      heap.set(index, tempA)
      tempA.index = index
      tempB.index = s
    }
    bubbleDown(s)
  }
}
