import { Component, ElementRef, OnInit, OnDestroy } from '@angular/core';
import ReactDOM from 'react-dom/client';
import { BrowserRouter, Routes, Route } from 'react-router-dom'; 
import Login from './Login';
import Welcome from '../welcome/Welcome';

@Component({
  selector: 'app-login-wrapper',
  template: '<div #reactRoot></div>', 
})
export class LoginWrapperComponent implements OnInit, OnDestroy {
  private root?: ReactDOM.Root;

  constructor(private host: ElementRef) {}

  ngOnInit(): void {
    const container = this.host.nativeElement.querySelector('div')!;
    this.root = ReactDOM.createRoot(container);
    this.root.render(
      <BrowserRouter> 
                <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/welcome" element={<Welcome />} />
          <Route path="*" element={<Login />} />
        </Routes>
      </BrowserRouter>
    );
  }

  ngOnDestroy(): void {
    this.root?.unmount();
  }
}