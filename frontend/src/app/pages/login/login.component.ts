import { Component, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { FeedbackComponent } from '../../shared/feedback.component';

@Component({
  selector: 'app-login', imports: [ReactiveFormsModule, FeedbackComponent],
  template: `
    <main class="login-page">
      <section class="login-card">
        <div class="login-intro"><span class="brand-mark large">GS</span><p class="eyebrow">PORTAL DE SOPORTE</p><h1>Bienvenido de nuevo</h1><p>Ingresa a tu cuenta para gestionar tus solicitudes.</p></div>
        <form [formGroup]="form" (ngSubmit)="submit()" novalidate>
          @if (error()) { <app-feedback type="error" [message]="error()" /> }
          <div class="field"><label for="correo">Correo electrónico</label><input id="correo" type="email" formControlName="correo" autocomplete="email" placeholder="nombre@empresa.com" />
            @if (invalid('correo')) { <small class="field-error">Ingresa un correo electrónico válido.</small> }</div>
          <div class="field"><label for="password">Contraseña</label><input id="password" type="password" formControlName="password" autocomplete="current-password" placeholder="Tu contraseña" />
            @if (invalid('password')) { <small class="field-error">La contraseña es obligatoria.</small> }</div>
          <button class="button primary full" type="submit" [disabled]="loading()">{{ loading() ? 'Ingresando…' : 'Ingresar al sistema' }}</button>
        </form>
        <p class="login-footer">Acceso seguro · Gestión de Soporte</p>
      </section>
    </main>`
})
export class LoginComponent {
  readonly loading = signal(false); readonly error = signal('');
  readonly form;
  constructor(fb: FormBuilder, private auth: AuthService, private router: Router) {
    this.form = fb.nonNullable.group({ correo: ['', [Validators.required, Validators.email]], password: ['', Validators.required] });
    if (auth.autenticado()) void router.navigateByUrl(auth.homeForRole());
  }
  invalid(name: 'correo'|'password'): boolean { const c=this.form.controls[name]; return c.invalid && (c.touched || this.form.touched); }
  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.loading.set(true); this.error.set('');
    this.auth.login(this.form.getRawValue()).pipe(finalize(() => this.loading.set(false))).subscribe({
      next: (session) => void this.router.navigateByUrl(this.auth.homeForRole(session.usuario.rol)),
      error: (e) => this.error.set(e.status === 401 ? 'El correo o la contraseña no son correctos.' : 'No pudimos iniciar sesión. Intenta nuevamente.')
    });
  }
}
