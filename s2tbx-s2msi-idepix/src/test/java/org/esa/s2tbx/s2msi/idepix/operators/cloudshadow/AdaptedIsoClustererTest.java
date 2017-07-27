package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author Tonio Fincke
 */
public class AdaptedIsoClustererTest {

    private AdaptedIsoClusterer clusterer;
    private List<Clusterable> list;

    @Before
    public void setUp() {
        clusterer = new AdaptedIsoClusterer(4, 30);
        list = new ArrayList<>();
    }

    @Test
    public void testCluster_OneBand() throws Exception {


        final double[][] values = new double[][]{{0.028711784604626533}, {0.03450325057661663}, {0.08324620504691782},
                {0.09316647688302504}, {0.10110824726904288}, {0.10601160470408544}, {0.10709009357269195},
                {0.10816701222573133}, {0.12144754170884198}, {0.13784896074458342}, {0.13795292095661127},
                {0.13802541427969073}, {0.15183648450577725}, {0.15768742752593778}, {0.1626159664271547},
                {0.16792264029870196}, {0.18199702112035565}, {0.18268689526903392}, {0.1885428334364957},
                {0.19147736157539708}, {0.19774586362609248}, {0.20154149346347883}, {0.2053443513923655},
                {0.21173675674421988}, {0.22478649778481175}, {0.2266832591517668}, {0.24632943391840934},
                {0.25245798143073817}, {0.2645763996256413}, {0.26808933630139753}, {0.27196740967019417},
                {0.27649343434583995}, {0.28298436428344176}, {0.28980813053168}, {0.3032719209162906},
                {0.3114990018034203}, {0.3127771058418216}, {0.3191920947726403}, {0.3360574126153003},
                {0.3709205271509701}, {0.3773109252874097}, {0.3871885185914503}, {0.42992451532088105},
                {0.4340827290818403}, {0.4370962694037057}, {0.46228701591818766}, {0.4640990862707891},
                {0.4804494035946618}, {0.4928979107914643}, {0.5068874417402915}, {0.5107025268848435},
                {0.5112183196749246}, {0.5133841354502622}, {0.5160822579075391}, {0.5168720092073089},
                {0.5395458168214095}, {0.5405016790823899}, {0.5438650971584402}, {0.5443795300607237},
                {0.580430848852936}, {0.5807846986019674}, {0.5866995547071289}, {0.5975787048478648},
                {0.6002093303243156}, {0.603400214014288}, {0.6043319822038017}, {0.6121093589448178},
                {0.6428498545890946}, {0.6527439890074476}, {0.659751198159602}, {0.6621693605044398},
                {0.6771098019661768}, {0.6801105226164738}, {0.6911884453120875}, {0.7235834584245509},
                {0.7269586203606083}, {0.7409040984150563}, {0.7615506739674159}, {0.7647659016195513},
                {0.7660809458727993}, {0.7667688422016536}, {0.7690073197731364}, {0.7696258200349527},
                {0.7724826090708728}, {0.7762279972422389}, {0.7858942695135156}, {0.8144208086979902},
                {0.8189939776096511}, {0.8558762478640034}, {0.8660826632273684}, {0.8672261218775388},
                {0.8734349307509978}, {0.8815040359627225}, {0.8847325300795152}, {0.8986966406075955},
                {0.9011945632891791}, {0.9149952979343327}, {0.9344179424569403}, {0.934517685149611},
                {0.9660811421324861}};


        for (double[] value : values) {
            list.add(new DoublePoint(value));
        }

        final List<CentroidCluster> clusters = clusterer.cluster(list);

        assertEquals(4, clusters.size());
        assertNotNull(clusters.get(0).getCenter());
        assertNotNull(clusters.get(0).getCenter().getPoint());
        assertEquals(1, clusters.get(0).getCenter().getPoint().length);
        assertEquals(0.14807247557284822, clusters.get(0).getCenter().getPoint()[0], 1e-8);
        assertNotNull(clusters.get(1).getCenter());
        assertNotNull(clusters.get(1).getCenter().getPoint());
        assertEquals(1, clusters.get(1).getCenter().getPoint().length);
        assertEquals(0.828308659389857, clusters.get(1).getCenter().getPoint()[0], 1e-8);
        assertNotNull(clusters.get(2).getCenter());
        assertNotNull(clusters.get(2).getCenter().getPoint());
        assertEquals(1, clusters.get(2).getCenter().getPoint().length);
        assertEquals(0.32484355320489855, clusters.get(2).getCenter().getPoint()[0], 1e-8);
        assertNotNull(clusters.get(3).getCenter());
        assertNotNull(clusters.get(3).getCenter().getPoint());
        assertEquals(1, clusters.get(3).getCenter().getPoint().length);
        assertEquals(0.5715393136281267, clusters.get(3).getCenter().getPoint()[0], 1e-8);
    }

    @Test
    public void testCluster_TwoBands() {
        final JDKRandomGenerator generator = new JDKRandomGenerator();

        double[] values = new double[100];
        for (int i = 0; i < 100; i++) {
            values[i] = generator.nextDouble();
        }

        Arrays.sort(values);

        final StringBuilder stringBuilder = new StringBuilder("{{");


        for (int i = 0; i < 100; i++) {
            stringBuilder.append(values[i]).append("}");
            if (i < 99) {
                stringBuilder.append(", {");
            }
        }
        stringBuilder.append("}");
        System.out.println(stringBuilder.toString());
    }


}