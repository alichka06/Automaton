package com.company;
import javafx.util.Pair;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.Comparator;

public class Automat
    {
    int alphabetLenght;
    TreeSet<Character> alphabet;
    int stateCount; // змінюється!!!
    TreeSet<Integer> state;
    int q0;
    int finalStateCount;
    int[] finalState;
    TreeSet <TransitionFunction> function;
    Automat(int a , int s, int _q0, int g)
    {
        alphabetLenght = a;
        alphabet = new TreeSet<Character>();
        stateCount = s;
        state = new TreeSet<Integer>();
        q0 = _q0;
        finalStateCount = g;
        finalState = new int[g];
        function = new TreeSet<TransitionFunction>(new MyComparator());
    }
    void setFinalState(int i, int t)
    {
        finalState[i] = t;
    }
    void setFunc(int st1, char a, int st2)
    {
        TransitionFunction tr = new TransitionFunction(st1, a, st2);
        function.add(tr);
        state.add(st1);
        state.add(st2);
        alphabet.add(a);
    }
    boolean isCorrect()
    {
        if (alphabetLenght < alphabet.size())
            return false;
        if (stateCount < state.size())
            return false;
        return true;
    }
    void stateOut()
    {
        for (TransitionFunction p: function)
        {
            System.out.println(p.getFrom()+" "+p.getChar()+" "+p.getTo());
        }
    }

    void inaccessible() //в які не існує шляху
    {
        ArrayList<Integer> visit = new ArrayList<Integer>();
        ArrayList<Integer> toRemove = new ArrayList<>();
        dfs(visit, q0);
        for (int i : state)
            if (!visit.contains(i))
                toRemove.add(i);
        for (int i : toRemove)
            state.remove(i);

        ArrayList<TransitionFunction> tr = new ArrayList<>();
        for (TransitionFunction a: function)
            if (toRemove.contains(a.getFrom()) || toRemove.contains(a.getTo()))
                tr.add(a);

        for (TransitionFunction a: tr)
            function.remove(a);
    }
    void dfs(ArrayList<Integer> vis, int q)
    {
        vis.add(q);
        for (TransitionFunction tr: function)
            if (tr.getFrom() == q && !vis.contains(tr.getTo()))
                dfs(vis, tr.getTo());
    }
    void dead_end() //тупикові - з яких немає шляху в фінальний стан
    {
        ArrayList<Integer> visit = new ArrayList<Integer>();
        ArrayList<TransitionFunction> tr = new ArrayList<>();
        ArrayList<Integer> remove = new ArrayList<Integer>();
        for (int st: finalState)
            if (!visit.contains(st))
                search(visit, st);
        for (int st: state)
            if (!visit.contains(st))
                remove.add(st);
        for (int i : remove)
            state.remove(i);
        for (TransitionFunction a: function)
            if (remove.contains(a.getFrom()) || remove.contains(a.getTo()))
                tr.add(a);
        for (TransitionFunction a: tr)
            function.remove(a);
    }
    void search(ArrayList<Integer> vis, int q)
    {
        vis.add(q);
        for (TransitionFunction tr: function)
            if (tr.getTo() == q && !vis.contains(tr.getFrom()))
                search(vis, tr.getFrom());
    }
    void equiv()
    {
        LinkedList <ArrayList<Integer>> R = new LinkedList <ArrayList<Integer>>();
        ArrayList<Integer> first = new ArrayList<>();
        ArrayList<Integer> second = new ArrayList<>();
        for (int i: finalState)
            first.add(i);
        for (int i: state)
            if (!first.contains(i)) second.add(i);
        R.add(first);
        R.add(second);
        LinkedList<Pair<ArrayList<Integer>, Character>> S = new LinkedList<>();
        for (char c: alphabet)
        {
            Pair<ArrayList<Integer>, Character> e = new Pair<>(R.get(0),c);
            S.add(e);
            e = new Pair<>(R.get(1),c);
            S.add(e);
        }
        while (!S.isEmpty())
        {
            Pair<ArrayList<Integer>, Character> e;
            e = S.get(0);
            S.remove(0);
            int j = R.size();
            int z=0;
            for (int i=0; i< j; i++)
            {
                Pair <ArrayList<Integer> , ArrayList<Integer>> pair = findEquivalence(R.get(z), e.getKey(), e.getValue());//???
                ArrayList<Integer> r1 = pair.getKey();
                ArrayList<Integer> r2 = pair.getValue();
                if (r1.size()!=0 && r2.size()!=0) {
                    R.add(r1);
                    R.add(r2);
                    for (char c: alphabet) {
                        Pair<ArrayList<Integer>, Character> p = new Pair<>(R.get(z), c);
                        if (S.contains(p))
                        {
                            S.remove(p);
                            Pair<ArrayList<Integer>, Character> p1 = new Pair<>(r1, c);
                            Pair<ArrayList<Integer>, Character> p2 = new Pair<>(r2, c);
                            S.add(p1);
                            S.add(p2);
                        }
                        else
                        {
                            Pair<ArrayList<Integer>, Character> p1 = new Pair<>(r1, c);
                            S.add(p1);
                        }
                    }
                    R.remove(z);
                } else z++;
            }
        }
        //outR(R);
        ArrayList<TransitionFunction> remove = new ArrayList<>();
        for (ArrayList<Integer> a: R)
            if (a.size()>1) {
                for (TransitionFunction tr : function)
                    if (a.contains(tr.getTo()) && tr.getTo() != a.get(0)) {
                        tr.setTo(a.get(0));
                        for (ArrayList<Integer> f : R)
                            if (f.contains(tr.getFrom()))
                                tr.setFrom(f.get(0));
                    }
                for (int i=1; i<a.size(); i++)
                    state.remove(a.get(i));
            }
    }
    Pair <ArrayList<Integer>, ArrayList<Integer>> findEquivalence (ArrayList<Integer> a, ArrayList<Integer> b, char c)
    {
        ArrayList<Integer> r1 = new ArrayList<>();
        ArrayList<Integer> r2 = new ArrayList<>();
        for (int i=0; i< a.size(); i++) {
            boolean flag = false;
            for (TransitionFunction tr: function) {
                if (tr.getChar() == c && a.get(i) == tr.getFrom()) {
                    if (b.contains(tr.getTo())) {
                        r1.add(a.get(i));
                        flag = true;
                    }
                }
            }
            if (!flag)
                r2.add(a.get(i));
        }
        Pair <ArrayList<Integer> , ArrayList<Integer>> pair = new Pair <ArrayList<Integer> , ArrayList<Integer>>(r1,r2);
        return pair;
    }
    void outR( LinkedList <ArrayList<Integer>> R)
    {
        for (int i=0; i<R.size(); i++)
        {
            for (int j=0; j<(R.get(i)).size(); j++)
                System.out.print((R.get(i)).get(j) + " ");
            System.out.print("\n");
        }
    }
    void outAvtomat()
    {
        System.out.println(alphabet.size());
        System.out.println(state.size());
        System.out.println(q0);
        TreeSet <TransitionFunction> newfunctions = new TreeSet<>(new MyComparator());
        ArrayList<Integer> newFinalState = new ArrayList<>();
        for (TransitionFunction p: function)
            newfunctions.add(p);
        for (TransitionFunction p: newfunctions)
            for (int i: finalState)
                if (p.getTo()==i && !newFinalState.contains(i))
                    newFinalState.add(i);
        System.out.print(newFinalState.size() + " ");
        for (int i: newFinalState)
            System.out.print(i + " ");
        System.out.print("\n");
        for (TransitionFunction p: newfunctions)
        {
            System.out.println(p.getFrom()+" "+p.getChar()+" "+p.getTo());
        }
    }
}

class MyComparator implements Comparator<TransitionFunction>{
    @Override
    public int compare(TransitionFunction o1, TransitionFunction o2) {
        if (o1.getFrom() < o2.getFrom()) return -1;
        if (o1.getFrom() > o2.getFrom()) return 1;
        if (o1.getTo() < o2.getTo()) return -1;
        if (o1.getTo() > o2.getTo()) return 1;
        return Character.compare(o1.getChar(), o2.getChar());
    }
}
class TransitionFunction {
    private int from;
    private char ch;
    private int to;
    TransitionFunction(int a, char c, int b)
    {
        from = a;
        ch = c;
        to = b;
    }
    int getFrom()
    {
        return from;
    }
    char getChar()
    {
        return ch;
    }
    int getTo()
    {
        return to;
    }
    void setTo(int s)
    {
        to = s;
    }
    void setFrom(int s)
    {
        from = s;
    }
}