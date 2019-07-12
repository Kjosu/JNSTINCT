package de.kjosu.jnstinct.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.kjosu.jnstinct.activation.Squash;
import de.kjosu.jnstinct.core.ConnectionGene;
import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.Neat;
import de.kjosu.jnstinct.core.NodeGene;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class GenomeLoader {

    private GenomeLoader() {

    }

    public static <T extends Genome<T>> void save(T genome, String fileName) throws IOException {
        save(genome, new File(fileName));
    }

    public static <T extends Genome<T>> void save(T genome, File file) throws IOException {
        if (genome == null || file == null) {
            throw new IllegalArgumentException();
        }

        GenomeStaff staff = new GenomeStaff(genome);
        ObjectMapper mapper = new ObjectMapper();

        mapper.writeValue(file, staff);
    }

    public static <T extends Genome<T>> T load(Neat<T> neat, String fileName) throws IOException {
        return load(neat, new File(fileName));
    }

    public static <T extends Genome<T>> T load(Neat<T> neat, File file) throws IOException {
        if (neat == null || file == null || !file.exists()) {
            throw new IllegalArgumentException();
        }

        ObjectMapper mapper = new ObjectMapper();
        GenomeStaff staff = mapper.readValue(file, GenomeStaff.class);

        T genome = neat.createGenome(neat, staff.getInputSize(), staff.getOutputSize(), false);

        for (NodeStaff nodeStaff : staff.getNodeStaffs()) {
            NodeGene node = new NodeGene(nodeStaff.getId(), nodeStaff.getType());
            node.setBias(nodeStaff.getBias());
            node.setSquash(nodeStaff.getSquash());
            node.setActivation(nodeStaff.getActivation());
            node.setState(nodeStaff.getState());
            node.setOld(nodeStaff.getOld());

            neat.registerNode(node);
            genome.getNodes().put(node.getId(), node);
        }

        for (ConnectionStaff conStaff : staff.getConnectionStaffs()) {
            ConnectionGene c = genome.connect(conStaff.getFromNode(), conStaff.getToNode(), conStaff.getWeight());
            c.setGain(conStaff.getGain());
            c.setEnabled(conStaff.isEnabled());
        }

        for (ConnectionStaff conStaff : staff.getSelfStaffs()) {
            ConnectionGene c = genome.connect(conStaff.getFromNode(), conStaff.getToNode(), conStaff.getWeight());
            c.setGain(conStaff.getGain());
            c.setEnabled(conStaff.isEnabled());
        }

        for (GateStaff conStaff : staff.getGateStaffs()) {
            genome.gate(genome.getNode(conStaff.getNodeID()), genome.getConnection(conStaff.getConnectionID()));
        }

        return genome;
    }

    private static class GenomeStaff {

        private int inputSize;
        private int outputSize;

        private List<NodeStaff> nodeStaffs = new Stack<>();
        private List<ConnectionStaff> connectionStaffs = new Stack<>();
        private List<GateStaff> gateStaffs = new Stack<>();
        private List<ConnectionStaff> selfStaffs = new Stack<>();

        public GenomeStaff(Genome<?> g) {
            inputSize = g.getInputSize();
            outputSize = g.getOutputSize();

            for (NodeGene node : g.getNodes().values()) {
                nodeStaffs.add(new NodeStaff(node));
            }

            for (ConnectionGene c : g.getConnections().values()) {
                connectionStaffs.add(new ConnectionStaff(c));
            }

            for (Map.Entry<ConnectionGene, Integer> entry : g.getGates().entrySet()) {
                gateStaffs.add(new GateStaff(entry.getKey().getId(), entry.getValue()));
            }

            for (ConnectionGene c : g.getSelfs().values()) {
                selfStaffs.add(new ConnectionStaff(c));
            }
        }

        public GenomeStaff() {

        }

        public int getInputSize() {
            return inputSize;
        }

        public void setInputSize(int inputSize) {
            this.inputSize = inputSize;
        }

        public int getOutputSize() {
            return outputSize;
        }

        public void setOutputSize(int outputSize) {
            this.outputSize = outputSize;
        }

        public List<NodeStaff> getNodeStaffs() {
            return nodeStaffs;
        }

        public void setNodeStaffs(List<NodeStaff> nodeStaffs) {
            this.nodeStaffs = nodeStaffs;
        }

        public List<ConnectionStaff> getConnectionStaffs() {
            return connectionStaffs;
        }

        public void setConnectionStaffs(List<ConnectionStaff> connectionStaffs) {
            this.connectionStaffs = connectionStaffs;
        }

        public List<GateStaff> getGateStaffs() {
            return gateStaffs;
        }

        public void setGateStaffs(List<GateStaff> gateStaffs) {
            this.gateStaffs = gateStaffs;
        }

        public List<ConnectionStaff> getSelfStaffs() {
            return selfStaffs;
        }

        public void setSelfStaffs(List<ConnectionStaff> selfStaffs) {
            this.selfStaffs = selfStaffs;
        }
    }

    private static class NodeStaff {

        private int id;
        private double bias;
        private Squash squash;
        private NodeGene.NodeType type;
        private double activation;
        private double state;
        private double old;

        public NodeStaff(NodeGene node) {
            id = node.getId();
            bias = node.getBias();
            squash = node.getSquash();
            type = node.getType();
            activation = node.getActivation();
            state = node.getState();
            old = node.getOld();
        }

        public NodeStaff() {

        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public double getBias() {
            return bias;
        }

        public void setBias(double bias) {
            this.bias = bias;
        }

        public Squash getSquash() {
            return squash;
        }

        public void setSquash(Squash squash) {
            this.squash = squash;
        }

        public NodeGene.NodeType getType() {
            return type;
        }

        public void setType(NodeGene.NodeType type) {
            this.type = type;
        }

        public double getActivation() {
            return activation;
        }

        public void setActivation(double activation) {
            this.activation = activation;
        }

        public double getState() {
            return state;
        }

        public void setState(double state) {
            this.state = state;
        }

        public double getOld() {
            return old;
        }

        public void setOld(double old) {
            this.old = old;
        }
    }

    private static class ConnectionStaff {

        private int fromNode;
        private int toNode;
        private double gain;
        private double weight;
        private boolean enabled;

        public ConnectionStaff(ConnectionGene c) {
            fromNode = c.getFromNode();
            toNode = c.getToNode();
            gain = c.getGain();
            weight = c.getWeight();
            enabled = c.isEnabled();
        }

        public ConnectionStaff() {

        }

        public int getFromNode() {
            return fromNode;
        }

        public void setFromNode(int fromNode) {
            this.fromNode = fromNode;
        }

        public int getToNode() {
            return toNode;
        }

        public void setToNode(int toNode) {
            this.toNode = toNode;
        }

        public double getGain() {
            return gain;
        }

        public void setGain(double gain) {
            this.gain = gain;
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    private static class GateStaff {

        private int connectionID;
        private int nodeID;

        public GateStaff(int connectionID, int nodeID) {
            this.connectionID = connectionID;
            this.nodeID = nodeID;
        }

        public GateStaff() {

        }

        public int getConnectionID() {
            return connectionID;
        }

        public void setConnectionID(int connectionID) {
            this.connectionID = connectionID;
        }

        public int getNodeID() {
            return nodeID;
        }

        public void setNodeID(int nodeID) {
            this.nodeID = nodeID;
        }
    }
}
