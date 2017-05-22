package org.esa.s2tbx.grm;

import org.esa.s2tbx.grm.segmentation.*;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Jean Coravu.
 */
public class NodeTest {

    public NodeTest() {
    }

    @Test
    public void testCheckLMBF() {
        BaatzSchapeNode firstNode = new BaatzSchapeNode(1, 0, 0, 0);
        BaatzSchapeNode secondNode = new BaatzSchapeNode(2, 1, 0, 0);
        BaatzSchapeNode thirdNode = new BaatzSchapeNode(3, 2, 0, 0);

        firstNode.addEdge(secondNode, 1);
        firstNode.addEdge(thirdNode, 1);

        secondNode.addEdge(firstNode, 1);
        secondNode.addEdge(thirdNode, 1);

        assertEquals(2, firstNode.getEdgeCount());
        assertEquals(2, secondNode.getEdgeCount());
        assertEquals(0, thirdNode.getEdgeCount());

        Node node = firstNode.checkLMBF(0.5f);
        assertNotNull(node);
        assertEquals(firstNode.getId(), node.getId());
    }

    @Test
    public void testRemoveEdge() {
        BaatzSchapeNode firstNode = new BaatzSchapeNode(1, 0, 0, 0);
        BaatzSchapeNode secondNode = new BaatzSchapeNode(2, 1, 0, 0);
        BaatzSchapeNode thirdNode = new BaatzSchapeNode(3, 2, 0, 0);

        firstNode.addEdge(secondNode, 1);
        firstNode.addEdge(thirdNode, 1);

        secondNode.addEdge(firstNode, 1);
        secondNode.addEdge(thirdNode, 1);

        assertEquals(2, firstNode.getEdgeCount());
        assertEquals(2, secondNode.getEdgeCount());

        int edgeIndex = firstNode.removeEdge(secondNode);
        assertTrue(edgeIndex >= 0);
    }

    @Test
    public void testFindEdge() {
        BaatzSchapeNode firstNode = new BaatzSchapeNode(1, 0, 0, 0);
        BaatzSchapeNode secondNode = new BaatzSchapeNode(2, 1, 0, 0);

        firstNode.addEdge(secondNode, 1);
        secondNode.addEdge(firstNode, 1);

        assertEquals(1, firstNode.getEdgeCount());
        assertEquals(1, secondNode.getEdgeCount());

        Edge edge = firstNode.findEdge(secondNode);
        assertNotNull(edge);
        assertNotNull(edge.getTarget());
        assertTrue(edge.getTarget() == secondNode);
    }

    @Test
    public void testUpdateInternalAttributes() {
        BaatzSchapeNode firstNode = new BaatzSchapeNode(1, 0, 0, 0);
        BaatzSchapeNode secondNode = new BaatzSchapeNode(2, 1, 0, 0);
        BaatzSchapeNode thirdNode = new BaatzSchapeNode(3, 2, 0, 0);

        firstNode.addEdge(secondNode, 1);
        firstNode.addEdge(thirdNode, 1);

        secondNode.addEdge(firstNode, 1);
        secondNode.addEdge(thirdNode, 1);

        assertEquals(2, firstNode.getEdgeCount());
        assertEquals(2, secondNode.getEdgeCount());

        firstNode.updateInternalAttributes(secondNode, 100);

        assertEquals(2, firstNode.getArea());
        assertEquals(6, firstNode.getPerimeter());

        assertFalse(firstNode.isValid());
        assertTrue(firstNode.isMerged());

        BoundingBox box = firstNode.getBox();
        assertNotNull(box);

        assertEquals(0, box.getLeftX());
        assertEquals(0, box.getTopY());
        assertEquals(2, box.getWidth());
        assertEquals(1, box.getHeight());

        Contour contour = firstNode.getContour();
        assertNotNull(contour);

        assertEquals(8, contour.size());
        assertEquals(Contour.RIGHT_MOVE_INDEX, contour.getMove(0));
        assertEquals(Contour.LEFT_MOVE_INDEX, contour.getMove(2));
        assertEquals(Contour.TOP_MOVE_INDEX, contour.getMove(4));
        assertEquals(Contour.TOP_MOVE_INDEX, contour.getMove(6));
    }
}
