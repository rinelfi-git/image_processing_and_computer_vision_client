/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emit.ipcv.engines.interfaces.observerPatterns;

/**
 *
 * @author rinelfi
 */
public interface MultipleValueObserver<V> {

  void update(V... values);
}
