package rbhide0.randomized_algorithms;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * Stable Marriage Problem as described in
 * "Randomized Algorithms" by Motwani and Raghavan.
 *
 * This solution randomly generates preferences for
 * the given number of men and women and generates
 * a marriage candidate. It then verifies that this
 * marriage would indeed be stable.
 *
 * @author Ravi Bhide (2011.12.19)
 */
public class StableMarriage {

        private static final int PARTNER_NONE = -1;
        private static final int RANK_NONE = -1;

        private static class Partner {
                // Id of the partner.
                int _id = PARTNER_NONE;

                // for men, rank of the partner considered so far.
                // For women, rank of the partner so far (the one in _id).
                int _rank = RANK_NONE;
        }

        private int _numCouples; // number of couples for this problem.
        private int[][] _menPreferences, _womenPreferences;
        private Partner[] _menPartners, _womenPartners; // current partners.

        public StableMarriage(int numberOfCouples) {
                _numCouples = numberOfCouples;

                // Define preference arrays for both men and women.
                int numMen = _numCouples;
                int numWomen = _numCouples;

                _menPreferences = new int[numMen][numWomen];
                _womenPreferences = new int[numWomen][numMen];

                _menPartners = new Partner[numMen];
                _womenPartners = new Partner[numWomen];

                // Create a collection that can be easily randomly shuffled.
                List<Integer> choices = new ArrayList<Integer>(_numCouples);
                for (int i=0; i<_numCouples; i++) {
                        choices.add(i);
                }

                // Initialize preference and partner arrays for each of the men.
                for (int i=0; i<numMen; i++) {
                        Collections.shuffle(choices);
                        _menPreferences[i] = new int[numWomen];
                        copy(choices, _menPreferences[i]);
                        _menPartners[i] = new Partner();
                }

                // Initialize preference and partner arrays for each of the women.
                for (int i=0; i<numWomen; i++) {
                        Collections.shuffle(choices);
                        _womenPreferences[i] = new int[numMen];
                        copy(choices, _womenPreferences[i]);
                        _womenPartners[i] = new Partner();
                }
        }

        // Copy a list to an array.
        private static void copy(List<Integer> list, int[] array) {
                if ((list != null) && (array != null) && (list.size() >= array.length)) {
                        for (int i=0; i<list.size(); i++) {
                                array[i] = list.get(i);
                        }
                }
        }

        // Solves the stable marriage problem.
        private void solve() {
                // Randomly order men and pick them in that order.
                List<Integer> menOrder = new ArrayList<Integer>(_numCouples);
                for (int i=0; i<_numCouples; i++) {
                        menOrder.add(i);
                }
                Collections.shuffle(menOrder);
                // We now have a random ordering of men.

                int numMen = _numCouples;
                int numWomen = _numCouples;

                boolean done = false;
                while (!done) {
                        // Find the first unengaged man.
                        int man = -1;
                        for (int i=0; i<numMen; i++) {
                                if (_menPartners[i]._id == PARTNER_NONE) {
                                        man = i;
                                        break;
                                }
                        }

                        if (man >= 0) {
                                // Propose to the highest preference woman,
                                // that this man has not proposed to yet.
                                Partner partner = _menPartners[man];
                                int[] preferenceList = _menPreferences[man];
                                for (int i=partner._rank+1; i<numWomen; i++) {
                                        if (propose(man, preferenceList[i])) {
                                                // Proposal accepted. No longer unengaged.
                                                // Congratulations!
                                                partner._rank = i;
                                                break;
                                        }
                                }
                        } else {
                                // No unengaged man found. We are done!
                                done = true;
                        }
                }
        }

        // Given man bends down on one knee and proposes to the given woman.
        // Returns true if the woman accepts the proposal, false otherwise.
        // This method has the side effect of storing the proposal, if accepted.
        private boolean propose(int man, int woman) {
                boolean proposalAccepted = false;

                Partner womansCurrentPartner = _womenPartners[woman];
                int womansPreferenceForMan = rank(man, woman, false); // lower value means higher preference.

                if (womansCurrentPartner._id == PARTNER_NONE) {
                        // Woman has no partner. She accepts the proposal.
                        womansCurrentPartner._id = man;
                        womansCurrentPartner._rank = womansPreferenceForMan;
                        proposalAccepted = true;
                } else if (womansPreferenceForMan < womansCurrentPartner._rank) {
                        // Woman accepts proposal since it is better than her current one.
                        // Her current fiancee becomes unengaged.
                        _menPartners[womansCurrentPartner._id]._id = PARTNER_NONE;
                        // Accept the new proposal.
                        womansCurrentPartner._id = man;
                        womansCurrentPartner._rank = womansPreferenceForMan;
                        proposalAccepted = true;
                }

                if (proposalAccepted) {
                        // Update man's partner status, since proposal was accepted.
                        Partner mansCurrentPartner = _menPartners[man];
                        mansCurrentPartner._id = woman;
                        mansCurrentPartner._rank = rank(man, woman, true);
                }

                return proposalAccepted;
        }

        // Gets the rank of the given man/woman for the given woman/man.
        // O(n) runtime complexity.
        private int rank(int man, int woman, boolean rankOfWomanForMan) {
                if (rankOfWomanForMan) {
                        int[] preferenceList = _menPreferences[man];
                        for (int i=0; i<preferenceList.length; i++) {
                                if (preferenceList[i] == woman) {
                                        return i;
                                }
                        }
                } else {
                        int[] preferenceList = _womenPreferences[woman];
                        for (int i=0; i<preferenceList.length; i++) {
                                if (preferenceList[i] == man) {
                                        return i;
                                }
                        }
                }
                return -1; // Never executed.
        }

        // Verifies that the given solution is a stable marriage.
        // Returns true if marriage is stable, false otherwise.
        public void verify() {
                for (int i=0; i<_numCouples; i++) {
                        if (!verifyMan(i) || !verifyWoman(i)) {
                                return;
                        }
                }

                System.out.println("Marriage is STABLE.");
        }

        // For the guy M1, there should be no woman W2
        // higher than his current fiancee W1,
        // such that W2 prefers M1 over her fiancee M2.
        private boolean verifyMan(int man) {
                Partner fiancee = _menPartners[man]; // W1
                int[] preferences = _menPreferences[man];

                // Get fiancee's rank for the man.
                int fianceeRank = rank(man, fiancee._id, true);

                // Get all women preferred over current fiancee.
                for (int i=0; i<fianceeRank; i++) {
                        int w2 = preferences[i]; // W2
                        int rankOfM1ForW2 = rank(man, w2, false);
                        Partner m2 = _womenPartners[w2]; // M2
                        int rankOfM2ForW2 = rank(m2._id, w2, false);
                        if (rankOfM1ForW2 < rankOfM2ForW2) { // M1 preferred over M2
                                System.err.println("Man[" + man + "]-Woman[" + fiancee._id + "] ");
                                System.err.println("Man[" + m2._id + "]-Woman[" + w2 + "] - UNSTABLE!");
                                System.err.println("Man[" + man + "] prefers Woman[" + w2 + "] (" + i + ") over Woman[" + fiancee._id + "] (" + fianceeRank + ")");
                                System.err.println("Woman[" + w2 + "] prefers Man[" + man + "] (" + rankOfM1ForW2 + ") over Man[" + m2._id + "] (" + rankOfM2ForW2 + ")");
                                System.err.println();
                                return false;
                        }
                }

                // No instability detected for this man.
                return true;
        }

        // For the woman W1, there should be no man M2
        // higher than her current fiancee M1,
        // such that M2 prefers W1 over his fiancee W2.
        private boolean verifyWoman(int woman) {
                Partner fiancee = _womenPartners[woman]; // M1
                int[] preferences = _womenPreferences[woman];

                // Get fiancee's rank for the woman.
                int fianceeRank = rank(fiancee._id, woman, false);

                // Get all men preferred over current fiancee.
                for (int i=0; i<fianceeRank; i++) {
                        int m2 = preferences[i]; // M2
                        int rankOfW1ForM2 = rank(m2, woman, true);
                        Partner w2 = _menPartners[m2]; // W2
                        int rankOfW2ForM2 = rank(m2, w2._id, true);
                        if (rankOfW1ForM2 < rankOfW2ForM2) { // W1 preferred over W2
                                System.err.println("Man[" + m2 + "]-Woman[" + w2._id + "]");
                                System.err.println("Man[" + fiancee._id + "]-Woman[" + woman + "]");
                                System.err.println("Man[" + m2 + "] prefers Woman[" + woman + "] (" + rankOfW1ForM2 + ") over Woman[" + w2._id + "] (" + rankOfW2ForM2 + ")");
                                System.err.println("Woman[" + woman + "] prefers Man[" + m2 + "] (" + i + ") over Man[" + fiancee._id + "] (" + fianceeRank + ")");
                                return false;
                        }
                }

                // No instability detected for this man.
                return true;
        }

        public static void main(String[] args) {
                if (args.length == 0) {
                        printUsage();
                } else {
                        try {
                                int numCouples = Integer.parseInt(args[0]);
                                StableMarriage sm = new StableMarriage(numCouples);
                                sm.solve();
                                System.out.println(sm);
                                sm.verify();
                        } catch (NumberFormatException e) {
                                printUsage();
                        }
                }
        }

        private static void printUsage() {
                System.err.println("Usage: java StableMarriage <number-of-couples>");
        }

        public String toString() {
                int numMen = _numCouples;
                int numWomen = _numCouples;

                StringBuilder sb = new StringBuilder();
                sb.append("Preferences for men\n");
                for (int i=0; i<numMen; i++) {
                        sb.append("Man[" + i + "]: ");
                        for (int j=0; j<numWomen; j++) {
                                sb.append(_menPreferences[i][j]);
                                sb.append(" ");
                        }
                        sb.append("\n");
                }

                sb.append("\nPreferences for women\n");
                for (int i=0; i<numWomen; i++) {
                        sb.append("Woman[" + i + "]: ");
                        for (int j=0; j<numMen; j++) {
                                sb.append(_womenPreferences[i][j]);
                                sb.append(" ");
                        }
                        sb.append("\n");
                }

                sb.append("\nSOLUTION\n");
                for (int i=0; i<numMen; i++) {
                        sb.append("Man[" + i + "]");
                        sb.append(" + ");
                        int woman = _menPartners[i]._id;
                        if (woman == PARTNER_NONE) {
                                sb.append("NONE");
                        } else {
                                sb.append("Woman[" + _menPartners[i]._id + "]");
                                int mansPreferenceOfWoman = rank(i, woman, true);
                                int womansPreferenceOfMan = rank(i, woman, false);
                                sb.append("\t[man-gets-preference: " + mansPreferenceOfWoman + ", woman-gets-preference : " + womansPreferenceOfMan + "]");
                        }
                        sb.append("\n");
                }

                return sb.toString();
        }
}
