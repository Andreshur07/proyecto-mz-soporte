import { Component, input } from '@angular/core';

@Component({ selector: 'app-feedback', template: `<div class="feedback" [class]="'feedback ' + type()" role="alert">{{ message() }}</div>` })
export class FeedbackComponent { readonly message = input.required<string>(); readonly type = input<'error'|'success'|'info'>('info'); }
