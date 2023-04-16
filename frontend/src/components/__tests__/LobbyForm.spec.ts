import { describe, it, expect } from 'vitest'

import { mount } from '@vue/test-utils'
import CreateLobbyForm from "../lobby/LobbyForm.vue";
import { createPinia, setActivePinia } from "pinia";
import { beforeEach} from "vitest";

describe('LobbyForm', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
  });

  it('renders properly when creating', () => {
    const wrapper = mount(CreateLobbyForm);
    expect(wrapper.text()).toContain('Create Lobby');
  });

  it('renders properly when joining', () => {
    const wrapper = mount(CreateLobbyForm, { props: { lobbyId: "7ea5f6e0-af1d-490b-a68e-4145f528a0ab" } });
    expect(wrapper.text()).toContain('Join Lobby');
  });
})
