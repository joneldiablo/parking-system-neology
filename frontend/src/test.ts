import 'zone.js/testing';
import { getTestBed } from '@angular/core/testing';
import {
  BrowserDynamicTestingModule,
  platformBrowserDynamicTesting
} from '@angular/platform-browser-dynamic/testing';

getTestBed().initTestEnvironment(
  BrowserDynamicTestingModule,
  platformBrowserDynamicTesting(),
  { teardown: { destroyAfterEach: true } }
);

// Specs de los componentes (import explícito en lugar de require.context,
// que no está disponible en el build webpack actual).
import './app/app.component.spec';
import './app/components/vehiculo-list/vehiculo-list.component.spec';
import './app/components/estancia-form/estancia-form.component.spec';