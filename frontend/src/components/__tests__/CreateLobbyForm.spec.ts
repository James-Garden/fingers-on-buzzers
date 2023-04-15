import { describe, it, expect } from 'vitest'

import { mount } from '@vue/test-utils'
import CreateLobbyForm from "../lobby/CreateLobbyForm.vue";
import { createPinia, setActivePinia } from "pinia";
import { beforeEach} from "vitest";

describe('CreateLobbyForm', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
  })

  it('renders properly', () => {
    const wrapper = mount(CreateLobbyForm);
    expect(wrapper.text()).toContain('Create Lobby');
  })
})
