package rbhide0.randomized_algorithms;

import java.util.List;

public class PermutationRoutingInHypercube {
	private int DIMENSIONS = 0;
	private int NUM_NODES = 1;
	
	private int _time = 0;
	
	private Message[] _message;
	private Node[] _node;
	
	public PermutationRoutingInHypercube(int dimension) {
		DIMENSIONS = dimension;
		NUM_NODES = 1 << DIMENSIONS;

		_node = new Node[NUM_NODES];
		_message = new Message[NUM_NODES];
	}
	
	public Node getNode(int i) {
		return _node[i];
	}
	
	public Message getMessage(int i) {
		return _message[i];
	}
	
	private void bitfix(Message m) {
	}
	
	private static class Message {
		int _source; // where did I originate from?
		int _destination; // where do I have to go, eventually?
		int _phase1Destination; // where do I have to go at the end of phase 1?
		int _currentLocation; // where am I at this time?
		
		int _timeToReachDest = -1; // amount of time it took me to reach my destination.
		
		public Message(int source) {
			_source = source;
			_currentLocation = source;
		}
		
		public void setDestination(int destination) {
			_destination = destination;
		}
		
		public void setPhase1Destination(int phase1Destination) {
			_phase1Destination = phase1Destination;
		}
		
		public boolean atDestination() {
			return _currentLocation == _destination;
		}
		
		public boolean atPhase1Destination() {
			return _currentLocation == _phase1Destination;
		}
		
		public void move() {
			if (!atDestination()) {
				int xor = _currentLocation ^ _destination;
				
			}
		}
	}
	
	private static class Node {
		int _location; // which node in the hypercube am I?
		List<Message> _queue; // list of messages in my queue.
	}
}
