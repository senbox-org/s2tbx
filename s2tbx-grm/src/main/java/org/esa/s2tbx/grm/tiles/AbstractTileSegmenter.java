package org.esa.s2tbx.grm.tiles;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.esa.s2tbx.grm.*;
import org.esa.snap.core.datamodel.Product;

import java.util.List;

/**
 * Created by jcoravu on 10/3/2017.
 */
public abstract class AbstractTileSegmenter {
    private Graph<?> graph;

    protected AbstractTileSegmenter() {
    }

//    const unsigned int niter,   //    m_NumberOfFirstIterations,
//    const unsigned int niter2, //    numberOfIterationsForPartialSegmentations,

    public void runFirstPartialSegmentation(List<ProcessingTile> tiles, float threshold, int imageWidth, int imageHeight, int tileWidth, int tileHeight, int margin) {
        int nbTilesX = imageWidth / tileWidth;
        if (imageWidth % tileWidth != 0) {
            nbTilesX++;
        }
        int nbTilesY = imageHeight / tileHeight;
        if (imageHeight % tileHeight != 0) {
            nbTilesY++;
        }

        boolean isFusion = false;
        int numberOfNeighborLayers = 123;// ????? static_cast<unsigned int>(pow(2, niter2 + 1) - 2);
        Product product = null;
        int bandIndices[] = null;
        int numberOfIterations = 0; // niter,
        boolean fastSegmentation = false;
        boolean addFourNeighbors = true;
        for(int row = 0; row < nbTilesY; ++row) {
            for (int col = 0; col<nbTilesX ; col++) {
                ProcessingTile currentTile = tiles.get(row*nbTilesX + col); // tiles[row*nbTilesX + col];

                AbstractSegmenter<?> segmenter = new SpringSegmenter(threshold);
                segmenter.update(product, bandIndices, numberOfIterations, fastSegmentation, addFourNeighbors);
                if (segmenter.isComplete()) {
                    isFusion = true;
                }

                // rescale the graph to be in the reference of the image
                rescaleGraph(currentTile, row, col, tileWidth, tileHeight, imageWidth);

                // remove unstable segments
                Graph<?> graph = segmenter.getGraph();
                graph.removeUnstableSegments(currentTile, imageWidth);

                // extract stability margin for all borders different from 0 imageWidth-1 and imageHeight -1 and write them to the stability margin
                Object2IntMap<Node> borderNodesMap = graph.detectBorderNodes(currentTile, imageWidth, imageHeight);

                extractStabilityMargin(borderNodesMap, numberOfNeighborLayers);
            }
        }
    }

    private void runPartialSegmentation(List<ProcessingTile> tiles, float threshold, int niter, int nbTilesX, int nbTilesY,
                                        int imageWidth, int imageHeight, int imageBands, boolean isFusion) {

        int numberOfNeighborLayers = 123;// ???? static_cast<unsigned int>(pow(2, niter + 1) - 2);
        for(int row = 0; row < nbTilesY; ++row) {
            for (int col = 0; col < nbTilesX; col++) {
                ProcessingTile currentTile = tiles.get(row * nbTilesX + col); // tiles[row*nbTilesX + col];

                readGraph(currentTile.nodeFileName, currentTile.edgeFileName);

                addStabilityMargin(tiles, row, col, nbTilesX, nbTilesY);

                Int2ObjectMap<List<Node>> borderPixelMap = graph.buildBorderPixelMap(currentTile,  row, col, nbTilesX, nbTilesY, imageWidth);
                graph.removeDuplicatedNodes(borderPixelMap, imageWidth);

                updateNeighborsOfNoneDuplicatedNodes(borderPixelMap, imageWidth, imageHeight);

                removeUselessNodes(currentTile, imageWidth, numberOfNeighborLayers);
            }
        }
    }

    private void updateNeighborsOfNoneDuplicatedNodes(Int2ObjectMap<List<Node>> borderPixelMap, int imageWidth, int imageHeight) {
        ObjectIterator<Int2ObjectMap.Entry<List<Node>>> it = borderPixelMap.int2ObjectEntrySet().iterator();
        while (it.hasNext()) {
            Int2ObjectMap.Entry<List<Node>> entry = it.next();
            int nodeId = entry.getIntKey();
            List<Node> nodes = entry.getValue();
            int[] neighborhood = new int[4];
            AbstractSegmenter.generateFourNeighborhood(neighborhood, nodeId, imageWidth, imageHeight);
            for(int j = 0; j < neighborhood.length; j++) {
                if (neighborhood[j] > -1) {
                    List<Node> neighborNodes = borderPixelMap.get(neighborhood[j]);
                    Node currentNode = nodes.get(0); // currNode
                    Node firstNeighborNode = neighborNodes.get(0); // neigh
                    if (currentNode != firstNeighborNode) {
                        Edge edge = currentNode.findEdge(firstNeighborNode);
                        if (edge == null) {
                            int boundary = 0;
                            IntSet borderCells = AbstractSegmenter.generateBorderCells(currentNode.getContour(), currentNode.getId(), imageWidth);
                            IntIterator itCells = borderCells.iterator();
                            while (itCells.hasNext()) {
                                int gridId = itCells.nextInt();
                                List<Node> resultNodes = borderPixelMap.get(gridId);
                                if (resultNodes != null) {
                                    int[] cellNeighborhood = new int[4];
                                    AbstractSegmenter.generateFourNeighborhood(cellNeighborhood, gridId, imageWidth, imageHeight);
                                    for(int k = 0; k < cellNeighborhood.length; k++) {
                                        if (cellNeighborhood[k] > -1) {
                                            List<Node> cellNeighborNodes = borderPixelMap.get(cellNeighborhood[k]);
                                            if (cellNeighborNodes != null && cellNeighborNodes.get(0) == firstNeighborNode) {
                                                boundary++;
                                            }
                                        }
                                    }
                                }
                            }
                            currentNode.addEdge(firstNeighborNode, boundary);
                            firstNeighborNode.addEdge(currentNode, boundary);
                        }
                    }
                }
            }
        }
    }

    private void insertNodesFromTile(ProcessingTile tile, boolean margin) {
        if (margin) {
            readGraph(tile.nodeMarginFileName, tile.edgeMarginFileName);
        } else {
            readGraph(tile.nodeFileName, tile.edgeFileName);
        }
    }

    private void readGraph(String nodesPath, String edgesPath) {

    }

    private void addStabilityMargin(List<ProcessingTile> tiles, int row, int col, int nbTilesX, int nbTilesY) {
        // margin to retrieve at top
        if (row > 0) {
            insertNodesFromTile(tiles.get((row-1) * nbTilesX + col), true);
        }
        // margin to retrieve at right
        if (col < nbTilesX - 1) {
            insertNodesFromTile(tiles.get(row * nbTilesX + (col+1)), true);
        }
        // margin to retrieve at bottom
        if (row < nbTilesY - 1) {
            insertNodesFromTile(tiles.get((row+1) * nbTilesX + col), true);
        }
        // margin to retrieve at left
        if (col > 0) {
            insertNodesFromTile(tiles.get(row * nbTilesX + (col-1)), true);
        }
        // margin to retrieve at top right
        if (row > 0 && col < nbTilesX - 1) {
            insertNodesFromTile(tiles.get((row-1) * nbTilesX + (col+1)), true);
        }
        // margin to retrieve at bottom right
        if (row < nbTilesY - 1 && col < nbTilesX - 1) {
            insertNodesFromTile(tiles.get((row+1) * nbTilesX + (col+1)), true);
        }
        // margin to retrieve at bottom left
        if (row < nbTilesY - 1 && col > 0) {
            insertNodesFromTile(tiles.get((row+1) * nbTilesX + (col-1)), true);
        }
        // margin to retrieve at top left
        if (row > 0 && col > 0) {
            insertNodesFromTile(tiles.get((row-1) * nbTilesX + (col-1)), true);
        }
    }

    private void removeUselessNodes(ProcessingTile tile, int imageWidth, int numberOfLayers) {
        Object2IntMap<Node> marginNodes = new Object2IntArrayMap<Node>();
        int nodeCount = this.graph.getNodeCount();
        for (int i = 0; i < nodeCount; i++) {
            Node node = this.graph.getNodeAt(i);
            BoundingBox box = node.getBox();

            if (box.getUpperLeftX() > tile.columns[0] && box.getUpperLeftY() > tile.rows[0] && box.getUpperRightX() - 1 < tile.columns[1] && box.getLowerRightY() - 1 < tile.rows[1]) {
                continue;
            } else if (box.getUpperLeftX() > tile.columns[1] || box.getUpperLeftY() > tile.rows[1] || box.getUpperRightX() - 1 < tile.columns[0] || box.getLowerRightY() - 1 < tile.rows[0]) {
                continue;
            } else {
                IntSet borderCells = AbstractSegmenter.generateBorderCells(node.getContour(), node.getId(), imageWidth);
                IntIterator it = borderCells.iterator();
                while (it.hasNext()) {
                    int gridId = it.nextInt();
                    int rowPixel = gridId / imageWidth;
                    int colPixel = gridId % imageWidth;
                    if (rowPixel == tile.rows[0] || rowPixel == tile.rows[1]) {
                        if (colPixel >= tile.columns[0] && colPixel <= tile.columns[1]) {
                            marginNodes.put(node, 0);
                            break;
                        }
                    } else if (colPixel == tile.columns[0] || colPixel == tile.columns[1]) {
                        if (rowPixel >= tile.rows[0] && rowPixel <= tile.rows[1]) {
                            marginNodes.put(node, 0);
                            break;
                        }
                    }
                }
            }
        }

        extractStabilityMargin(marginNodes, numberOfLayers);

        nodeCount = this.graph.getNodeCount();
        for (int i = 0; i < nodeCount; i++) {
            Node node = this.graph.getNodeAt(i);
            BoundingBox box = node.getBox();

            if (box.getUpperLeftX() > tile.columns[0] && box.getUpperLeftY() > tile.rows[0] && box.getUpperRightX() - 1 < tile.columns[1] && box.getLowerRightY() - 1 < tile.rows[1]) {
                continue;
            } else if (marginNodes.containsKey(node)) {
                graph.removeEdgeToUnstableNode(node);
                node.setExpired(true);
            }
        }

        graph.removeExpiredNodes();
    }

    private void extractStabilityMargin(Object2IntMap<Node> borderNodesMap, int pmax) {
        ObjectIterator<Object2IntMap.Entry<Node>> it = borderNodesMap.object2IntEntrySet().iterator();
        while (it.hasNext()) {
            Object2IntMap.Entry<Node> entry = it.next();
            Node node = entry.getKey();
            exploreDFS(node, 0, borderNodesMap, pmax);
        }
    }

    private void exploreDFS(Node node, int p, Object2IntMap<Node> borderNodesMap, int pmax) {
        if (p > pmax) {
            return;
        } else {
            if (borderNodesMap.containsKey(node)) {
                int value = borderNodesMap.getInt(node);
                if (p <= value) {
                    borderNodesMap.put(node, p);
                    int edgeCount = node.getEdgeCount();
                    for (int i=0; i<edgeCount; i++) {
                        Edge edge = node.getEdgeAt(i);
                        exploreDFS(edge.getTarget(), p + 1, borderNodesMap, pmax);
                    }
                } else {
                    return;
                }
            } else{
                borderNodesMap.put(node, p);
                int edgeCount = node.getEdgeCount();
                for (int i=0; i<edgeCount; i++) {
                    Edge edge = node.getEdgeAt(i);
                    exploreDFS(edge.getTarget(), p + 1, borderNodesMap, pmax);
                }
            }
        }
    }

    private void removeUnstableSegments(Graph<?> graph, ProcessingTile tile, int imageWidth) {
        int rowPixel, colPixel;
        boolean stable = false;

    }

    private void rescaleGraph(ProcessingTile tile, int rowTile, int colTile, int tileWidth, int tileHeight, int imageWidth) {
        int rowNodeTile, colNodeTile;
        int rowNodeImg, colNodeImg;

//        for(auto& node : graph.m_Nodes)
//        {
//            // Start pixel index of the node (in the tile)
//            rowNodeTile = node->m_Id / tile.region.GetSize()[0];
//            colNodeTile = node->m_Id % tile.region.GetSize()[0];
//
//            // Start pixel index of the node (in the image)
//            rowNodeImg = rowTile * tileHeight + rowNodeTile - tile.margin[0];
//            colNodeImg = colTile * tileWidth + colNodeTile - tile.margin[3];
//            node->m_Id = rowNodeImg * imageWidth + colNodeImg;
//
//            // Change also its bounding box
//            node->m_Bbox.m_UX = colTile * tileWidth + node->m_Bbox.m_UX - tile.margin[3];
//            node->m_Bbox.m_UY = rowTile * tileHeight + node->m_Bbox.m_UY - tile.margin[0];
//        }
    }
}
