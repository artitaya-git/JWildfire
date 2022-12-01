/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2016 Andreas Maschke

  This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser 
  General Public License as published by the Free Software Foundation; either version 2.1 of the 
  License, or (at your option) any later version.
 
  This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without 
  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License along with this software; 
  if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jwildfire.create.tina.randomflame;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.*;
import org.jwildfire.create.tina.mutagen.PainterlyStyleMutation;
import org.jwildfire.create.tina.randomgradient.RandomGradientGenerator;

import java.util.ArrayList;
import java.util.List;


public class PainterlyRandomFlameGenerator extends RandomFlameGenerator {
  private static List<RandomFlameGenerator> generators;

  static {
    generators = new ArrayList<RandomFlameGenerator>();
    generators.add(new BrokatRandomFlameGenerator());
    generators.add(new Brokat3DRandomFlameGenerator());
    generators.add(new BubblesRandomFlameGenerator());
    generators.add(new DualityRandomFlameGenerator());
    generators.add(new Bubbles3DRandomFlameGenerator());
    generators.add(new CrossRandomFlameGenerator());
    generators.add(new DualityRandomFlameGenerator());
    generators.add(new GalaxiesRandomFlameGenerator());
    generators.add(new DuckiesRandomFlameGenerator());
    generators.add(new ExperimentalBubbles3DRandomFlameGenerator());
    generators.add(new ExperimentalGnarlRandomFlameGenerator());
    generators.add(new ExperimentalSimpleRandomFlameGenerator());
    generators.add(new FilledFlowers3DRandomFlameGenerator());
    generators.add(new Flowers3DRandomFlameGenerator());
    generators.add(new GnarlRandomFlameGenerator());
    generators.add(new DualityRandomFlameGenerator());
    generators.add(new GalaxiesRandomFlameGenerator());
    generators.add(new Gnarl3DRandomFlameGenerator());
    generators.add(new JulianDiscRandomFlameGenerator());
    generators.add(new JuliansRandomFlameGenerator());
    generators.add(new LayersRandomFlameGenerator());
    generators.add(new Affine3DRandomFlameGenerator());
    generators.add(new JulianRingsRandomFlameGenerator());
    generators.add(new LinearRandomFlameGenerator());
    generators.add(new MachineRandomFlameGenerator());
    generators.add(new MandelbrotRandomFlameGenerator());
    generators.add(new OutlinesRandomFlameGenerator());
    generators.add(new PhoenixRandomFlameGenerator());
    generators.add(new RasterRandomFlameGenerator());
    generators.add(new RaysRandomFlameGenerator());
    generators.add(new RunRandomScriptRandomFlameGenerator());
    generators.add(new SimpleRandomFlameGenerator());
    generators.add(new SimpleTilingRandomFlameGenerator());
    generators.add(new SierpinskyRandomFlameGenerator());
    generators.add(new DualityRandomFlameGenerator());
    generators.add(new GalaxiesRandomFlameGenerator());
    generators.add(new SphericalRandomFlameGenerator());
    generators.add(new Spherical3DRandomFlameGenerator());
    generators.add(new GhostsRandomFlameGenerator());
    generators.add(new OrchidsRandomFlameGenerator());
    generators.add(new EDiscRandomFlameGenerator());
    generators.add(new SpiralsRandomFlameGenerator());
    generators.add(new Spirals3DRandomFlameGenerator());
    generators.add(new SplitsRandomFlameGenerator());
    generators.add(new SubFlameRandomFlameGenerator());
    generators.add(new SynthRandomFlameGenerator());
    generators.add(new TentacleRandomFlameGenerator());
    generators.add(new TileBallRandomFlameGenerator());
    generators.add(new DualityRandomFlameGenerator());
    generators.add(new XenomorphRandomFlameGenerator());
    generators.add(new BlackAndWhiteRandomFlameGenerator());
  }

  private static final String PAINTERLY_RANDGEN = "PAINTERLY_RANDGEN";

  @Override
  public RandomFlameGeneratorState initState(Prefs pPrefs, RandomGradientGenerator pRandomGradientGenerator) {
    RandomFlameGeneratorState state = super.initState(pPrefs, pRandomGradientGenerator);
    RandomFlameGenerator generator = generators.get((int) (Math.random() * generators.size()));
    state.getParams().put(PAINTERLY_RANDGEN, generator);
    return state;
  }

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    RandomFlameGenerator generator = createRandGen(pState);
    RandomFlameGeneratorState subState = generator.initState(pState.getPrefs(), pState.getGradientGenerator());
    Flame flame = generator.prepareFlame(subState);
    flame.setName(getName() + " - " + flame.hashCode());
    return flame;
  }

  private RandomFlameGenerator createRandGen(RandomFlameGeneratorState pState) {
    RandomFlameGenerator generator = (RandomFlameGenerator) pState.getParams().get(PAINTERLY_RANDGEN);
    return generator;
  }

  @Override
  public String getName() {
    return "Painterly";
  }

  @Override
  public boolean isUseFilter(RandomFlameGeneratorState pState) {
    return true;
  }

  @Override
  protected Flame postProcessFlameBeforeRendering(RandomFlameGeneratorState pState, Flame pFlame) {
    for (Layer layer : pFlame.getLayers()) {
      new PainterlyStyleMutation().execute(layer, 1.0);
    }
    return pFlame;
  }

  @Override
  protected Flame postProcessFlameAfterRendering(RandomFlameGeneratorState pState, Flame pFlame) {
    return pFlame;
  }
}
